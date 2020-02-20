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

package de.adorsys.psd2.consent.service;

import de.adorsys.psd2.consent.api.CmsError;
import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.WrongChecksumException;
import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.consent.api.consent.CmsCreateConsentResponse;
import de.adorsys.psd2.consent.api.service.ConsentService;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentServiceInternalEncryptedTest {
    private static final String ENCRYPTED_CONSENT_ID = "encrypted consent id";
    private static final String MALFORMED_ENCRYPTED_CONSENT_ID = "malformed encrypted consent id";
    private static final String CONSENT_ID = "consent id";

    @Mock
    private SecurityDataService securityDataService;
    @Mock
    private ConsentService consentService;

    @InjectMocks
    private ConsentServiceInternalEncrypted consentServiceInternalEncrypted;

    @Test
    void createConsent() throws WrongChecksumException {
        CmsConsent newCmsConsent = new CmsConsent();
        CmsConsent createdCmsConsent = new CmsConsent();
        when(consentService.createConsent(newCmsConsent))
            .thenReturn(CmsResponse.<CmsCreateConsentResponse>builder()
                            .payload(new CmsCreateConsentResponse(CONSENT_ID, createdCmsConsent))
                            .build());
        when(securityDataService.encryptId(CONSENT_ID)).thenReturn(Optional.of(ENCRYPTED_CONSENT_ID));

        CmsResponse<CmsCreateConsentResponse> response = consentServiceInternalEncrypted.createConsent(newCmsConsent);

        assertTrue(response.isSuccessful());
        CmsCreateConsentResponse payload = response.getPayload();
        assertEquals(ENCRYPTED_CONSENT_ID, payload.getConsentId());
        assertEquals(createdCmsConsent, payload.getCmsConsent());
        verify(consentService).createConsent(newCmsConsent);
    }

    @Test
    void createConsent_serviceError() throws WrongChecksumException {
        CmsConsent newCmsConsent = new CmsConsent();
        when(consentService.createConsent(newCmsConsent))
            .thenReturn(CmsResponse.<CmsCreateConsentResponse>builder()
                            .error(CmsError.LOGICAL_ERROR)
                            .build());

        CmsResponse<CmsCreateConsentResponse> response = consentServiceInternalEncrypted.createConsent(newCmsConsent);

        assertTrue(response.hasError());
        assertEquals(CmsError.LOGICAL_ERROR, response.getError());
        verify(consentService).createConsent(newCmsConsent);
    }

    @Test
    void createConsent_encryptionError() throws WrongChecksumException {
        CmsConsent newCmsConsent = new CmsConsent();
        CmsConsent createdCmsConsent = new CmsConsent();
        when(consentService.createConsent(newCmsConsent))
            .thenReturn(CmsResponse.<CmsCreateConsentResponse>builder()
                            .payload(new CmsCreateConsentResponse(CONSENT_ID, createdCmsConsent))
                            .build());
        when(securityDataService.encryptId(CONSENT_ID)).thenReturn(Optional.empty());

        CmsResponse<CmsCreateConsentResponse> response = consentServiceInternalEncrypted.createConsent(newCmsConsent);

        assertTrue(response.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, response.getError());
        verify(consentService).createConsent(newCmsConsent);
    }

    @Test
    void getConsentStatusById() {
        ConsentStatus consentStatus = ConsentStatus.VALID;
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID)).thenReturn(Optional.of(CONSENT_ID));
        when(consentService.getConsentStatusById(CONSENT_ID))
            .thenReturn(CmsResponse.<ConsentStatus>builder()
                            .payload(consentStatus)
                            .build());

        CmsResponse<ConsentStatus> response = consentServiceInternalEncrypted.getConsentStatusById(ENCRYPTED_CONSENT_ID);

        assertTrue(response.isSuccessful());
        assertEquals(consentStatus, response.getPayload());
        verify(consentService).getConsentStatusById(CONSENT_ID);
    }

    @Test
    void getConsentStatusById_malformedEncryptedId() {
        when(securityDataService.decryptId(MALFORMED_ENCRYPTED_CONSENT_ID)).thenReturn(Optional.empty());

        CmsResponse<ConsentStatus> response = consentServiceInternalEncrypted.getConsentStatusById(MALFORMED_ENCRYPTED_CONSENT_ID);

        assertTrue(response.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, response.getError());
        verify(consentService, never()).getConsentStatusById(any());
    }

    @Test
    void updateConsentStatusById() throws WrongChecksumException {
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID)).thenReturn(Optional.of(CONSENT_ID));
        ConsentStatus consentStatus = ConsentStatus.VALID;
        when(consentService.updateConsentStatusById(CONSENT_ID, consentStatus))
            .thenReturn(CmsResponse.<Boolean>builder()
                            .payload(true)
                            .build());

        CmsResponse<Boolean> response = consentServiceInternalEncrypted.updateConsentStatusById(ENCRYPTED_CONSENT_ID, consentStatus);

        assertTrue(response.isSuccessful());
        assertTrue(response.getPayload());
        verify(consentService).updateConsentStatusById(CONSENT_ID, consentStatus);
    }

    @Test
    void updateConsentStatusById_malformedEncryptedId() throws WrongChecksumException {
        when(securityDataService.decryptId(MALFORMED_ENCRYPTED_CONSENT_ID)).thenReturn(Optional.empty());
        ConsentStatus consentStatus = ConsentStatus.VALID;

        CmsResponse<Boolean> response = consentServiceInternalEncrypted.updateConsentStatusById(MALFORMED_ENCRYPTED_CONSENT_ID, consentStatus);

        assertTrue(response.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, response.getError());
        verify(consentService, never()).updateConsentStatusById(any(), any());
    }

    @Test
    void getConsentById() {
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID)).thenReturn(Optional.of(CONSENT_ID));
        CmsConsent cmsConsent = new CmsConsent();
        when(consentService.getConsentById(CONSENT_ID))
            .thenReturn(CmsResponse.<CmsConsent>builder()
                            .payload(cmsConsent)
                            .build());

        CmsResponse<CmsConsent> response = consentServiceInternalEncrypted.getConsentById(ENCRYPTED_CONSENT_ID);

        assertTrue(response.isSuccessful());
        assertEquals(cmsConsent, response.getPayload());
        verify(consentService).getConsentById(CONSENT_ID);
    }

    @Test
    void getConsentById_malformedEncryptedId() {
        when(securityDataService.decryptId(MALFORMED_ENCRYPTED_CONSENT_ID)).thenReturn(Optional.empty());

        CmsResponse<CmsConsent> response = consentServiceInternalEncrypted.getConsentById(MALFORMED_ENCRYPTED_CONSENT_ID);

        assertTrue(response.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, response.getError());
        verify(consentService, never()).getConsentById(any());
    }

    @Test
    void findAndTerminateOldConsentsByNewConsentId() {
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID)).thenReturn(Optional.of(CONSENT_ID));
        when(consentService.findAndTerminateOldConsentsByNewConsentId(CONSENT_ID))
            .thenReturn(CmsResponse.<Boolean>builder()
                            .payload(true)
                            .build());

        CmsResponse<Boolean> response = consentServiceInternalEncrypted.findAndTerminateOldConsentsByNewConsentId(ENCRYPTED_CONSENT_ID);

        assertTrue(response.isSuccessful());
        assertTrue(response.getPayload());
        verify(consentService).findAndTerminateOldConsentsByNewConsentId(CONSENT_ID);
    }

    @Test
    void findAndTerminateOldConsentsByNewConsentId_malformedEncryptedId() {
        when(securityDataService.decryptId(MALFORMED_ENCRYPTED_CONSENT_ID)).thenReturn(Optional.empty());

        CmsResponse<Boolean> response = consentServiceInternalEncrypted.findAndTerminateOldConsentsByNewConsentId(MALFORMED_ENCRYPTED_CONSENT_ID);

        assertTrue(response.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, response.getError());
        verify(consentService, never()).findAndTerminateOldConsentsByNewConsentId(any());
    }

    @Test
    void getPsuDataByConsentId() {
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID)).thenReturn(Optional.of(CONSENT_ID));
        List<PsuIdData> psuIdDataList = Collections.singletonList(new PsuIdData());
        when(consentService.getPsuDataByConsentId(CONSENT_ID))
            .thenReturn(CmsResponse.<List<PsuIdData>>builder()
                            .payload(psuIdDataList)
                            .build());

        CmsResponse<List<PsuIdData>> response = consentServiceInternalEncrypted.getPsuDataByConsentId(ENCRYPTED_CONSENT_ID);

        assertTrue(response.isSuccessful());
        assertEquals(psuIdDataList, response.getPayload());
        verify(consentService).getPsuDataByConsentId(CONSENT_ID);
    }

    @Test
    void getPsuDataByConsentId_malformedEncryptedId() {
        when(securityDataService.decryptId(MALFORMED_ENCRYPTED_CONSENT_ID)).thenReturn(Optional.empty());

        CmsResponse<List<PsuIdData>> response = consentServiceInternalEncrypted.getPsuDataByConsentId(MALFORMED_ENCRYPTED_CONSENT_ID);

        assertTrue(response.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, response.getError());
        verify(consentService, never()).getPsuDataByConsentId(any());
    }

    @Test
    void updateMultilevelScaRequired() throws WrongChecksumException {
        when(securityDataService.decryptId(ENCRYPTED_CONSENT_ID)).thenReturn(Optional.of(CONSENT_ID));
        when(consentService.updateMultilevelScaRequired(CONSENT_ID, true))
            .thenReturn(CmsResponse.<Boolean>builder()
                            .payload(true)
                            .build());

        CmsResponse<Boolean> response = consentServiceInternalEncrypted.updateMultilevelScaRequired(ENCRYPTED_CONSENT_ID, true);

        assertTrue(response.isSuccessful());
        assertTrue(response.getPayload());
        verify(consentService).updateMultilevelScaRequired(CONSENT_ID, true);
    }

    @Test
    void updateMultilevelScaRequired_malformedEncryptedId() throws WrongChecksumException {
        when(securityDataService.decryptId(MALFORMED_ENCRYPTED_CONSENT_ID)).thenReturn(Optional.empty());

        CmsResponse<Boolean> response = consentServiceInternalEncrypted.updateMultilevelScaRequired(MALFORMED_ENCRYPTED_CONSENT_ID, true);

        assertTrue(response.hasError());
        assertEquals(CmsError.TECHNICAL_ERROR, response.getError());
        verify(consentService, never()).updateMultilevelScaRequired(any(), anyBoolean());
    }
}
