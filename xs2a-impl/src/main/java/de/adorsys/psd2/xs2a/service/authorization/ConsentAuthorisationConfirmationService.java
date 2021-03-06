/*
 * Copyright 2018-2020 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.psd2.xs2a.service.authorization;

import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.service.AuthorisationServiceEncrypted;
import de.adorsys.psd2.core.data.Consent;
import de.adorsys.psd2.xs2a.core.authorisation.Authorisation;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.domain.ErrorHolder;
import de.adorsys.psd2.xs2a.core.error.ErrorType;
import de.adorsys.psd2.xs2a.core.mapper.ServiceType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import de.adorsys.psd2.xs2a.domain.ResponseObject;
import de.adorsys.psd2.xs2a.domain.consent.UpdateConsentPsuDataReq;
import de.adorsys.psd2.xs2a.domain.consent.UpdateConsentPsuDataResponse;
import de.adorsys.psd2.xs2a.service.context.SpiContextDataProvider;
import de.adorsys.psd2.xs2a.service.mapper.spi_xs2a_mappers.SpiErrorMapper;
import de.adorsys.psd2.xs2a.service.profile.AspspProfileServiceWrapper;
import de.adorsys.psd2.xs2a.service.spi.SpiAspspConsentDataProviderFactory;
import de.adorsys.psd2.xs2a.spi.domain.SpiAspspConsentDataProvider;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiCheckConfirmationCodeRequest;
import de.adorsys.psd2.xs2a.spi.domain.consent.SpiConsentConfirmationCodeValidationResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static de.adorsys.psd2.xs2a.core.domain.TppMessageInformation.of;
import static de.adorsys.psd2.xs2a.core.error.MessageErrorCode.CONSENT_UNKNOWN_403;
import static de.adorsys.psd2.xs2a.core.error.MessageErrorCode.SCA_INVALID;

@Slf4j
@RequiredArgsConstructor
public abstract class ConsentAuthorisationConfirmationService<T extends Consent> {

    private final AspspProfileServiceWrapper aspspProfileServiceWrapper;
    private final SpiContextDataProvider spiContextDataProvider;
    private final SpiAspspConsentDataProviderFactory aspspConsentDataProviderFactory;
    private final Xs2aAuthorisationService authorisationService;
    private final SpiErrorMapper spiErrorMapper;
    private final AuthorisationServiceEncrypted authorisationServiceEncrypted;

    /**
     * Checks authorisation confirmation data. Has two possible flows:
     * - data is checked at XS2A side, we compare the data from DB with the incoming data;
     * - data is transferred to SPI level and checking should be implemented at ASPSP side.
     *
     * @param request {@link UpdateConsentPsuDataReq} with all consent information.
     * @return {@link UpdateConsentPsuDataResponse} with new authorisation status.
     */
    public ResponseObject<UpdateConsentPsuDataResponse> processAuthorisationConfirmation(UpdateConsentPsuDataReq request) {
        String authorisationId = request.getAuthorisationId();

        CmsResponse<Authorisation> authorisationCmsResponse = authorisationServiceEncrypted.getAuthorisationById(authorisationId);

        if (authorisationCmsResponse.hasError()) {
            log.info("Authorisation-ID: [{}]. Update consent PSU data failed: authorisation not found by ID",
                     request.getAuthorizationId());
            return ResponseObject.<UpdateConsentPsuDataResponse>builder()
                       .fail(getErrorType403(), of(CONSENT_UNKNOWN_403)).build();
        }

        Authorisation authorisation = authorisationCmsResponse.getPayload();

        ScaStatus currentStatus = authorisation.getScaStatus();
        boolean processIsAllowed = currentStatus == ScaStatus.UNCONFIRMED;

        UpdateConsentPsuDataResponse response = processIsAllowed
                                                    ? processAuthorisationConfirmationInternal(request, authorisation.getScaAuthenticationData())
                                                    : buildScaConfirmationCodeErrorResponse(request.getConsentId(), authorisationId, request.getPsuData());

        return Optional.ofNullable(response.getErrorHolder())
                   .map(e -> ResponseObject.<UpdateConsentPsuDataResponse>builder()
                                 .fail(e)
                                 .build())
                   .orElseGet(ResponseObject.<UpdateConsentPsuDataResponse>builder().body(response)::build);
    }

    private UpdateConsentPsuDataResponse processAuthorisationConfirmationInternal(UpdateConsentPsuDataReq request, String confirmationCodeFromDb) {
        return aspspProfileServiceWrapper.isAuthorisationConfirmationCheckByXs2a()
                   ? checkAuthorisationConfirmationXs2a(request, confirmationCodeFromDb)
                   : checkAuthorisationConfirmationOnSpi(request);
    }

    private UpdateConsentPsuDataResponse checkAuthorisationConfirmationXs2a(UpdateConsentPsuDataReq request, String confirmationCodeFromDb) {
        String consentId = request.getConsentId();
        String authorisationId = request.getAuthorisationId();
        PsuIdData psuData = request.getPsuData();

        Optional<T> consentOptional = getConsentById(consentId);
        if (consentOptional.isEmpty()) {
            return buildConsentNotFoundErrorResponse(consentId, authorisationId, psuData);
        }

        SpiAspspConsentDataProvider aspspConsentDataProvider = aspspConsentDataProviderFactory.getSpiAspspDataProviderFor(consentId);
        boolean codeCorrect = checkConfirmationCodeInternally(authorisationId, request.getConfirmationCode(), confirmationCodeFromDb, aspspConsentDataProvider);

        SpiResponse<SpiConsentConfirmationCodeValidationResponse> spiResponse =
            notifyConfirmationCodeValidation(spiContextDataProvider.provideWithPsuIdData(psuData),
                                             codeCorrect, consentOptional.get(),
                                             aspspConsentDataProvider);

        if (spiResponse.hasError()) {
            return buildConfirmationCodeSpiErrorResponse(spiResponse, consentId, authorisationId, psuData);
        }

        SpiConsentConfirmationCodeValidationResponse confirmationCodeValidationResponse = spiResponse.getPayload();
        UpdateConsentPsuDataResponse response = codeCorrect
                                                    ? new UpdateConsentPsuDataResponse(confirmationCodeValidationResponse.getScaStatus(),
                                                                                       consentId,
                                                                                       authorisationId,
                                                                                       psuData)
                                                    : buildScaConfirmationCodeErrorResponse(consentId, authorisationId, psuData);

        if (spiResponse.isSuccessful()) {
            authorisationService.updateAuthorisationStatus(authorisationId, confirmationCodeValidationResponse.getScaStatus());
            updateConsentStatus(consentId, confirmationCodeValidationResponse.getConsentStatus());
        }

        return response;
    }

    private UpdateConsentPsuDataResponse checkAuthorisationConfirmationOnSpi(UpdateConsentPsuDataReq request) {
        String consentId = request.getConsentId();
        String authorisationId = request.getAuthorisationId();

        Optional<T> consentOptional = getConsentById(consentId);

        if (consentOptional.isEmpty()) {
            return buildConsentNotFoundErrorResponse(consentId, authorisationId, request.getPsuData());
        }

        SpiCheckConfirmationCodeRequest spiCheckConfirmationCodeRequest = new SpiCheckConfirmationCodeRequest(request.getConfirmationCode(), authorisationId);

        SpiResponse<SpiConsentConfirmationCodeValidationResponse> spiResponse = checkConfirmationCode(spiContextDataProvider.provideWithPsuIdData(request.getPsuData()),
                                                                                                      spiCheckConfirmationCodeRequest,
                                                                                                      aspspConsentDataProviderFactory.getSpiAspspDataProviderFor(consentId));

        UpdateConsentPsuDataResponse updateConsentPsuDataResponse;
        if (spiResponse.hasError()) {
            updateConsentStatus(consentId, ConsentStatus.REJECTED);
            updateConsentPsuDataResponse = buildConfirmationCodeSpiErrorResponse(spiResponse, consentId, authorisationId, request.getPsuData());
        } else {
            SpiConsentConfirmationCodeValidationResponse confirmationCodeValidationResponse = spiResponse.getPayload();
            updateConsentStatus(consentId, confirmationCodeValidationResponse.getConsentStatus());
            updateConsentPsuDataResponse = new UpdateConsentPsuDataResponse(confirmationCodeValidationResponse.getScaStatus(),
                                                                            consentId,
                                                                            authorisationId,
                                                                            request.getPsuData());
        }

        authorisationService.updateAuthorisationStatus(authorisationId, updateConsentPsuDataResponse.getScaStatus());
        return updateConsentPsuDataResponse;
    }

    private UpdateConsentPsuDataResponse buildScaConfirmationCodeErrorResponse(String consentId, String authorisationId, PsuIdData psuIdData) {
        ErrorHolder errorHolder = ErrorHolder.builder(getErrorType400())
                                      .tppMessages(of(SCA_INVALID))
                                      .build();

        log.info("Authorisation-ID: [{}]. Update consent PSU data failed: confirmation code is wrong or has been provided more than once.", authorisationId);

        return new UpdateConsentPsuDataResponse(errorHolder, consentId, authorisationId, psuIdData);
    }

    private UpdateConsentPsuDataResponse buildConfirmationCodeSpiErrorResponse(SpiResponse<SpiConsentConfirmationCodeValidationResponse> spiResponse,
                                                                               String consentId, String authorisationId, PsuIdData psuIdData) {
        ErrorHolder errorHolder = spiErrorMapper.mapToErrorHolder(spiResponse, getServiceType());

        log.info("Authorisation-ID: [{}]. Update consent PSU data failed: error occurred at SPI.", authorisationId);

        return new UpdateConsentPsuDataResponse(errorHolder, consentId, authorisationId, psuIdData);
    }

    private UpdateConsentPsuDataResponse buildConsentNotFoundErrorResponse(String consentId, String authorisationId, PsuIdData psuIdData) {
        ErrorHolder errorHolder = ErrorHolder.builder(getErrorType403())
                                      .tppMessages(of(CONSENT_UNKNOWN_403))
                                      .build();

        log.info("Consent-ID: [{}]. Update consent PSU data failed: consent not found by id", consentId);

        return new UpdateConsentPsuDataResponse(errorHolder, consentId, authorisationId, psuIdData);
    }

    protected abstract void updateConsentStatus(String consentId, ConsentStatus consentStatus);

    protected abstract SpiResponse<SpiConsentConfirmationCodeValidationResponse> notifyConfirmationCodeValidation(SpiContextData spiContextData, boolean isCodeCorrect, T consent, SpiAspspConsentDataProvider spiAspspConsentDataProvider);

    protected abstract Optional<T> getConsentById(String consentId);

    protected abstract SpiResponse<SpiConsentConfirmationCodeValidationResponse> checkConfirmationCode(SpiContextData spiContextData, SpiCheckConfirmationCodeRequest spiCheckConfirmationCodeRequest, SpiAspspConsentDataProvider spiAspspConsentDataProvider);

    protected abstract boolean checkConfirmationCodeInternally(String authorisationId, String confirmationCode, String scaAuthenticationData, SpiAspspConsentDataProvider aspspConsentDataProvider);

    protected abstract ServiceType getServiceType();

    protected abstract ErrorType getErrorType400();

    protected abstract ErrorType getErrorType403();
}
