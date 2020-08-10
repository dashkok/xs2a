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

package de.adorsys.psd2.xs2a.service;

import de.adorsys.psd2.consent.api.CmsError;
import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.WrongChecksumException;
import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.consent.api.authorisation.CreateAuthorisationRequest;
import de.adorsys.psd2.consent.api.authorisation.CreateAuthorisationResponse;
import de.adorsys.psd2.consent.api.authorisation.UpdateAuthorisationRequest;
import de.adorsys.psd2.consent.api.consent.CmsCreateConsentResponse;
import de.adorsys.psd2.consent.api.service.AisConsentServiceEncrypted;
import de.adorsys.psd2.consent.api.service.ConsentServiceEncrypted;
import de.adorsys.psd2.core.data.AccountAccess;
import de.adorsys.psd2.core.data.piis.v1.PiisConsent;
import de.adorsys.psd2.logger.context.LoggingContextService;
import de.adorsys.psd2.xs2a.core.authorisation.AuthorisationType;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.domain.account.Xs2aCreatePiisConsentResponse;
import de.adorsys.psd2.xs2a.domain.consent.UpdateConsentPsuDataReq;
import de.adorsys.psd2.xs2a.domain.fund.CreatePiisConsentRequest;
import de.adorsys.psd2.xs2a.service.authorization.Xs2aAuthorisationService;
import de.adorsys.psd2.xs2a.service.consent.Xs2aPiisConsentService;
import de.adorsys.psd2.xs2a.service.mapper.cms_xs2a_mappers.Xs2aConsentAuthorisationMapper;
import de.adorsys.psd2.xs2a.service.mapper.cms_xs2a_mappers.Xs2aPiisConsentMapper;
import de.adorsys.xs2a.reader.JsonReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static de.adorsys.psd2.consent.api.CmsError.LOGICAL_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Xs2aPiisConsentServiceTest {
    private static final String CORRECT_PSU_ID = "marion.mueller";
    private static final String CONSENT_ID = "consent ID";
    private static final String AUTHORISATION_ID = "a01562ea-19ff-4b5a-8188-c45d85bfa20a";
    private static final String REDIRECT_URI = "request/redirect_uri";
    private static final String NOK_REDIRECT_URI = "request/nok_redirect_uri";
    private static final ScaApproach SCA_APPROACH = ScaApproach.DECOUPLED;
    private static final ScaStatus SCA_STATUS = ScaStatus.RECEIVED;
    private static final PsuIdData PSU_ID_DATA = new PsuIdData(CORRECT_PSU_ID, null, null, null, null);
    private static final CreateAuthorisationRequest CONSENT_AUTHORISATION_REQUEST = buildConsentAuthorisationRequest();
    private final TppInfo tppInfo = buildTppInfo();

    @InjectMocks
    private Xs2aPiisConsentService xs2aPiisConsentService;
    @Mock
    private Xs2aPiisConsentMapper xs2aPiisConsentMapper;
    @Mock
    private CmsConsent cmsConsent;
    @Mock
    private PiisConsent piisConsent;
    @Mock
    private ConsentServiceEncrypted consentService;
    @Mock
    private LoggingContextService loggingContextService;
    @Mock
    private AisConsentServiceEncrypted aisConsentServiceEncrypted;
    @Mock
    private Xs2aAuthorisationService authorisationService;
    @Mock
    private Xs2aConsentAuthorisationMapper consentAuthorisationMapper;
    @Mock
    private ScaApproachResolver scaApproachResolver;
    @Mock
    private RequestProviderService requestProviderService;

    private JsonReader jsonReader = new JsonReader();

    @Test
    void createConsent_success() throws WrongChecksumException {
        //Given
        CreatePiisConsentRequest request = new CreatePiisConsentRequest(null, null, null, null, null);
        when(xs2aPiisConsentMapper.mapToCmsConsent(request, PSU_ID_DATA, tppInfo))
            .thenReturn(cmsConsent);
        CmsResponse<CmsCreateConsentResponse> response = CmsResponse.<CmsCreateConsentResponse>builder().payload(new CmsCreateConsentResponse(CONSENT_ID, cmsConsent)).build();
        when(consentService.createConsent(cmsConsent))
            .thenReturn(response);
        when(xs2aPiisConsentMapper.mapToPiisConsent(cmsConsent))
            .thenReturn(piisConsent);

        //When
        Optional<Xs2aCreatePiisConsentResponse> xs2aCreatePiisConsentResponseOptional = xs2aPiisConsentService.createConsent(request, PSU_ID_DATA, tppInfo);

        //Then
        assertTrue(xs2aCreatePiisConsentResponseOptional.isPresent());
        Xs2aCreatePiisConsentResponse xs2aCreatePiisConsentResponse = xs2aCreatePiisConsentResponseOptional.get();
        assertEquals(CONSENT_ID, xs2aCreatePiisConsentResponse.getConsentId());
        assertEquals(piisConsent, xs2aCreatePiisConsentResponse.getPiisConsent());
    }

    @Test
    void createConsent_catchWrongChecksumException() throws WrongChecksumException {
        //Given
        CreatePiisConsentRequest request = new CreatePiisConsentRequest(null, null, null, null, null);
        when(xs2aPiisConsentMapper.mapToCmsConsent(request, PSU_ID_DATA, tppInfo))
            .thenReturn(cmsConsent);
        when(consentService.createConsent(cmsConsent))
            .thenThrow(new WrongChecksumException());

        //When
        Optional<Xs2aCreatePiisConsentResponse> xs2aCreatePiisConsentResponseOptional = xs2aPiisConsentService.createConsent(request, PSU_ID_DATA, tppInfo);

        //Then
        assertTrue(xs2aCreatePiisConsentResponseOptional.isEmpty());
    }

    @Test
    void createConsent_withError() throws WrongChecksumException {
        //Given
        CreatePiisConsentRequest request = new CreatePiisConsentRequest(null, null, null, null, null);
        when(xs2aPiisConsentMapper.mapToCmsConsent(request, PSU_ID_DATA, tppInfo))
            .thenReturn(cmsConsent);
        when(consentService.createConsent(cmsConsent))
            .thenReturn(CmsResponse.<CmsCreateConsentResponse>builder()
                            .error(LOGICAL_ERROR)
                            .build());

        //When
        Optional<Xs2aCreatePiisConsentResponse> xs2aCreatePiisConsentResponseOptional = xs2aPiisConsentService.createConsent(request, PSU_ID_DATA, tppInfo);

        //Then
        assertTrue(xs2aCreatePiisConsentResponseOptional.isEmpty());
    }

    @Test
    void getPiisConsentById_success() {
        //Given
        when(consentService.getConsentById(CONSENT_ID))
            .thenReturn(CmsResponse.<CmsConsent>builder().payload(cmsConsent).build());
        when(xs2aPiisConsentMapper.mapToPiisConsent(cmsConsent))
            .thenReturn(piisConsent);
        //When
        Optional<PiisConsent> piisConsentById = xs2aPiisConsentService.getPiisConsentById(CONSENT_ID);
        //Then
        assertTrue(piisConsentById.isPresent());
        assertEquals(piisConsent, piisConsentById.get());
    }

    @Test
    void getPiisConsentById_withError() {
        //Given
        when(consentService.getConsentById(CONSENT_ID))
            .thenReturn(CmsResponse.<CmsConsent>builder().error(LOGICAL_ERROR).build());
        //When
        Optional<PiisConsent> piisConsentById = xs2aPiisConsentService.getPiisConsentById(CONSENT_ID);
        //Then
        assertTrue(piisConsentById.isEmpty());
    }

    @Test
    void updateConsentStatusById_success() throws WrongChecksumException {
        //Given
        CmsResponse<Boolean> cmsResponse = CmsResponse.<Boolean>builder()
                                               .payload(true)
                                               .build();
        when(consentService.updateConsentStatusById(CONSENT_ID, ConsentStatus.PARTIALLY_AUTHORISED)).thenReturn(cmsResponse);
        //When
        xs2aPiisConsentService.updateConsentStatus(CONSENT_ID, ConsentStatus.PARTIALLY_AUTHORISED);
        //Then
        verify(consentService, atLeastOnce()).updateConsentStatusById(CONSENT_ID, ConsentStatus.PARTIALLY_AUTHORISED);
    }

    @Test
    void updateConsentStatus_WrongChecksumException() throws WrongChecksumException {
        // Given
        when(consentService.updateConsentStatusById(CONSENT_ID, ConsentStatus.PARTIALLY_AUTHORISED))
            .thenThrow(new WrongChecksumException());

        // When
        xs2aPiisConsentService.updateConsentStatus(CONSENT_ID, ConsentStatus.PARTIALLY_AUTHORISED);

        // Then
        verify(consentService, times(1)).updateConsentStatusById(CONSENT_ID, ConsentStatus.PARTIALLY_AUTHORISED);
        verify(loggingContextService, never()).storeConsentStatus(any(ConsentStatus.class));
    }

    @Test
    void updateMultilevelScaRequired() throws WrongChecksumException {
        //Given
        //When
        xs2aPiisConsentService.updateMultilevelScaRequired(CONSENT_ID, true);
        //Then
        verify(consentService, atLeastOnce()).updateMultilevelScaRequired(CONSENT_ID, true);
    }

    @Test
    void updateMultilevelScaRequired_throwWrongChecksumException() throws WrongChecksumException {
        //Given
        doThrow(new WrongChecksumException()).when(consentService).updateMultilevelScaRequired(CONSENT_ID, true);
        //When
        xs2aPiisConsentService.updateMultilevelScaRequired(CONSENT_ID, true);
        //Then
        verify(consentService, atLeastOnce()).updateMultilevelScaRequired(CONSENT_ID, true);
    }

    @Test
    void updateAspspAccountAccess() throws WrongChecksumException {
        AccountAccess accountAccess = jsonReader.getObjectFromFile("json/aspect/account-access.json", AccountAccess.class);

        CmsConsent cmsConsent = new CmsConsent();
        when(aisConsentServiceEncrypted.updateAspspAccountAccess(CONSENT_ID, accountAccess))
            .thenReturn(CmsResponse.<CmsConsent>builder().payload(cmsConsent).build());
        when(xs2aPiisConsentMapper.mapToPiisConsent(cmsConsent)).thenReturn(piisConsent);

        CmsResponse<PiisConsent> actual = xs2aPiisConsentService.updateAspspAccountAccess(CONSENT_ID, accountAccess);

        assertFalse(actual.hasError());
        assertEquals(piisConsent, actual.getPayload());
    }

    @Test
    void updateAspspAccountAccess_checksumError() throws WrongChecksumException {
        AccountAccess accountAccess = jsonReader.getObjectFromFile("json/aspect/account-access.json", AccountAccess.class);

        when(aisConsentServiceEncrypted.updateAspspAccountAccess(CONSENT_ID, accountAccess))
            .thenThrow(new WrongChecksumException());

        CmsResponse<PiisConsent> actual = xs2aPiisConsentService.updateAspspAccountAccess(CONSENT_ID, accountAccess);

        assertTrue(actual.hasError());
        assertEquals(CmsError.CHECKSUM_ERROR, actual.getError());
    }

    @Test
    void updateAspspAccountAccess_updateError() throws WrongChecksumException {
        AccountAccess accountAccess = jsonReader.getObjectFromFile("json/aspect/account-access.json", AccountAccess.class);

        when(aisConsentServiceEncrypted.updateAspspAccountAccess(CONSENT_ID, accountAccess))
            .thenReturn(CmsResponse.<CmsConsent>builder().error(CmsError.TECHNICAL_ERROR).build());

        CmsResponse<PiisConsent> actual = xs2aPiisConsentService.updateAspspAccountAccess(CONSENT_ID, accountAccess);

        assertTrue(actual.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, actual.getError());
    }

    @Test
    void findAndTerminateOldConsentsByNewConsentId_success() {
        // Given
        when(consentService.findAndTerminateOldConsentsByNewConsentId(CONSENT_ID))
            .thenReturn(CmsResponse.<Boolean>builder().payload(true).build());

        // When
        boolean actualResponse = xs2aPiisConsentService.findAndTerminateOldConsentsByNewConsentId(CONSENT_ID);

        // Then
        assertThat(actualResponse).isTrue();
    }

    @Test
    void findAndTerminateOldConsentsByNewConsentId_false() {
        // Given
        when(consentService.findAndTerminateOldConsentsByNewConsentId(CONSENT_ID))
            .thenReturn(CmsResponse.<Boolean>builder().payload(false).build());

        // When
        boolean actualResponse = xs2aPiisConsentService.findAndTerminateOldConsentsByNewConsentId(CONSENT_ID);

        // Then
        assertThat(actualResponse).isFalse();
    }

    @Test
    void updateConsentAuthorization() {
        // Given
        UpdateConsentPsuDataReq updateConsentPsuDataReq = new UpdateConsentPsuDataReq();
        updateConsentPsuDataReq.setAuthorizationId(AUTHORISATION_ID);
        UpdateAuthorisationRequest request = new UpdateAuthorisationRequest();

        when(consentAuthorisationMapper.mapToAuthorisationRequest(updateConsentPsuDataReq))
            .thenReturn(request);

        // When
        xs2aPiisConsentService.updateConsentAuthorisation(updateConsentPsuDataReq);

        // Then
        verify(authorisationService, times(1)).updateAuthorisation(request, AUTHORISATION_ID);
    }

    @Test
    void updateConsentAuthorization_nullValue() {
        // When
        xs2aPiisConsentService.updateConsentAuthorisation(null);

        // Then
        verify(authorisationService, never()).updateAuthorisation(any(), any());
    }


    @Test
    void createConsentAuthorization_success() {
        // Given
        when(scaApproachResolver.resolveScaApproach())
            .thenReturn(SCA_APPROACH);
        when(consentAuthorisationMapper.mapToAuthorisationRequest(SCA_STATUS, PSU_ID_DATA   , SCA_APPROACH, REDIRECT_URI, NOK_REDIRECT_URI))
            .thenReturn(CONSENT_AUTHORISATION_REQUEST);
        when(authorisationService.createAuthorisation(CONSENT_AUTHORISATION_REQUEST, CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(Optional.of(buildCreateConsentAuthorizationResponse()));
        when(requestProviderService.getTppRedirectURI())
            .thenReturn(REDIRECT_URI);
        when(requestProviderService.getTppNokRedirectURI())
            .thenReturn(NOK_REDIRECT_URI);

        // When
        Optional<CreateAuthorisationResponse> actualResponse = xs2aPiisConsentService.createConsentAuthorisation(CONSENT_ID, SCA_STATUS, PSU_ID_DATA);
        CreateAuthorisationResponse expected = buildCreateConsentAuthorizationResponse();

        // Then
        assertThat(actualResponse.isPresent()).isTrue();
        assertThat(actualResponse.get()).isEqualTo(expected);
    }

    @Test
    void createConsentAuthorization_false() {
        // Given
        when(requestProviderService.getTppRedirectURI()).thenReturn("ok.uri");
        when(requestProviderService.getTppNokRedirectURI()).thenReturn("nok.uri");

        when(scaApproachResolver.resolveScaApproach()).thenReturn(SCA_APPROACH);
        CreateAuthorisationRequest request = new CreateAuthorisationRequest();
        when(consentAuthorisationMapper.mapToAuthorisationRequest(SCA_STATUS, PSU_ID_DATA, SCA_APPROACH, "ok.uri", "nok.uri"))
            .thenReturn(request);
        when(authorisationService.createAuthorisation(request, CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(Optional.empty());

        // When
        Optional<CreateAuthorisationResponse> actualResponse = xs2aPiisConsentService.createConsentAuthorisation(CONSENT_ID, SCA_STATUS, PSU_ID_DATA);

        // Then
        assertThat(actualResponse.isPresent()).isFalse();
    }

    private TppInfo buildTppInfo() {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setAuthorisationNumber("Test TppId");
        return tppInfo;
    }

    private static CreateAuthorisationRequest buildConsentAuthorisationRequest() {
        CreateAuthorisationRequest consentAuthorization = new CreateAuthorisationRequest();
        consentAuthorization.setPsuData(PSU_ID_DATA);
        consentAuthorization.setScaApproach(SCA_APPROACH);
        return consentAuthorization;
    }

    private static CreateAuthorisationResponse buildCreateConsentAuthorizationResponse() {
        return new CreateAuthorisationResponse(AUTHORISATION_ID, ScaStatus.RECEIVED, "", null);
    }
}
