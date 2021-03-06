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

package de.adorsys.psd2.consent.service.aspsp;

import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.domain.AuthorisationEntity;
import de.adorsys.psd2.consent.domain.consent.ConsentEntity;
import de.adorsys.psd2.consent.repository.AuthorisationRepository;
import de.adorsys.psd2.consent.repository.ConsentJpaRepository;
import de.adorsys.psd2.consent.repository.specification.AisConsentSpecification;
import de.adorsys.psd2.consent.service.mapper.AisConsentMapper;
import de.adorsys.psd2.consent.service.migration.AisConsentLazyMigrationService;
import de.adorsys.psd2.consent.service.psu.util.PageRequestBuilder;
import de.adorsys.psd2.xs2a.core.authorisation.AuthorisationType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.xs2a.reader.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CmsAspspAisExportServiceInternalTest {
    public static final Integer PAGE_INDEX = 0;
    public static final Integer ITEMS_PER_PAGE = 20;
    private static final String TPP_AUTHORISATION_NUMBER = "authorisation number";
    private static final String WRONG_TPP_AUTHORISATION_NUMBER = "wrong authorisation number";
    private static final LocalDate CREATION_DATE_FROM = LocalDate.of(2019, 1, 1);
    private static final LocalDate CREATION_DATE_TO = LocalDate.of(2020, 12, 1);
    private static final String DEFAULT_SERVICE_INSTANCE_ID = "UNDEFINED";
    private static final String PSU_ID = "anton.brueckner";
    private static final String WRONG_PSU_ID = "max.musterman";
    private static final String EXTERNAL_CONSENT_ID = "4b112130-6a96-4941-a220-2da8a4af2c65";
    private static final String ASPSP_ACCOUNT_ID = "3278921mxl-n2131-13nw";
    private static final String WRONG_ASPSP_ACCOUNT_ID = "00000000aa-n2131-13nw";
    private static final OffsetDateTime CREATION_DATE_TIME = OffsetDateTime.now();
    private static final OffsetDateTime STATUS_CHANGE_DATE_TIME = OffsetDateTime.now();

    private PsuIdData psuIdData;
    private PsuIdData wrongPsuIdData;
    private JsonReader jsonReader = new JsonReader();

    @InjectMocks
    private CmsAspspAisExportServiceInternal cmsAspspAisExportServiceInternal;

    @Mock
    private AisConsentSpecification aisConsentSpecification;
    @Mock
    private ConsentJpaRepository consentJpaRepository;
    @Mock
    private AisConsentMapper aisConsentMapper;
    @Mock
    private AuthorisationRepository authorisationRepository;
    @Mock
    private AisConsentLazyMigrationService aisConsentLazyMigrationService;
    @Mock
    private PageRequestBuilder pageRequestBuilder;

    @BeforeEach
    void setUp() {
        psuIdData = buildPsuIdData(PSU_ID);
        wrongPsuIdData = buildPsuIdData(WRONG_PSU_ID);
    }

    @Test
    void exportConsentsByTpp_success() {
        // Given
        ConsentEntity consentEntity = buildConsentEntity();

        when(aisConsentSpecification.byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(
            TPP_AUTHORISATION_NUMBER,
            CREATION_DATE_FROM,
            CREATION_DATE_TO,
            psuIdData,
            DEFAULT_SERVICE_INSTANCE_ID
        )).thenReturn((root, criteriaQuery, criteriaBuilder) -> null);
        //noinspection unchecked
        when(consentJpaRepository.findAll(any(Specification.class)))
            .thenReturn(Collections.singletonList(consentEntity));
        CmsAisAccountConsent expectedConsent = buildAisAccountConsent();

        List<AuthorisationEntity> authorisations = Collections.singletonList(new AuthorisationEntity());
        when(authorisationRepository.findAllByParentExternalIdAndType(EXTERNAL_CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(authorisations);
        when(aisConsentLazyMigrationService.migrateIfNeeded(consentEntity))
            .thenReturn(consentEntity);
        when(aisConsentMapper.mapToCmsAisAccountConsent(consentEntity, authorisations))
            .thenReturn(buildAisAccountConsent());

        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByTpp(TPP_AUTHORISATION_NUMBER, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, psuIdData, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertFalse(aisConsents.isEmpty());
        assertTrue(aisConsents.contains(expectedConsent));
        verify(aisConsentSpecification, times(1))
            .byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(TPP_AUTHORISATION_NUMBER, CREATION_DATE_FROM,
                                                               CREATION_DATE_TO, psuIdData, DEFAULT_SERVICE_INSTANCE_ID);
    }

    @Test
    void exportConsentsByTpp_successPagination() {
        // Given
        ConsentEntity consentEntity = buildConsentEntity();

        when(aisConsentSpecification.byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(
            TPP_AUTHORISATION_NUMBER,
            CREATION_DATE_FROM,
            CREATION_DATE_TO,
            psuIdData,
            DEFAULT_SERVICE_INSTANCE_ID
        )).thenReturn((root, criteriaQuery, criteriaBuilder) -> null);
        //noinspection unchecked
        PageRequest pageRequest = PageRequest.of(PAGE_INDEX, ITEMS_PER_PAGE);
        when(pageRequestBuilder.getPageParams(PAGE_INDEX, ITEMS_PER_PAGE)).thenReturn(pageRequest);
        when(consentJpaRepository.findAll(any(Specification.class), eq(pageRequest)))
            .thenReturn(new PageImpl<>(Collections.singletonList(consentEntity)));
        CmsAisAccountConsent expectedConsent = buildAisAccountConsent();

        List<AuthorisationEntity> authorisations = Collections.singletonList(new AuthorisationEntity());
        when(authorisationRepository.findAllByParentExternalIdAndType(EXTERNAL_CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(authorisations);
        when(aisConsentLazyMigrationService.migrateIfNeeded(consentEntity))
            .thenReturn(consentEntity);
        when(aisConsentMapper.mapToCmsAisAccountConsent(consentEntity, authorisations))
            .thenReturn(buildAisAccountConsent());

        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByTpp(TPP_AUTHORISATION_NUMBER, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, psuIdData, DEFAULT_SERVICE_INSTANCE_ID, PAGE_INDEX, ITEMS_PER_PAGE);

        // Then
        assertFalse(aisConsents.isEmpty());
        assertTrue(aisConsents.contains(expectedConsent));
        verify(aisConsentSpecification, times(1))
            .byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(TPP_AUTHORISATION_NUMBER, CREATION_DATE_FROM,
                                                               CREATION_DATE_TO, psuIdData, DEFAULT_SERVICE_INSTANCE_ID);
    }

    @Test
    void exportConsentsByTpp_failure_wrongTppAuthorisationNumber() {
        // Given
        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByTpp(WRONG_TPP_AUTHORISATION_NUMBER, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, psuIdData, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertTrue(aisConsents.isEmpty());
        verify(aisConsentSpecification, times(1))
            .byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(WRONG_TPP_AUTHORISATION_NUMBER, CREATION_DATE_FROM,
                                                               CREATION_DATE_TO, psuIdData, DEFAULT_SERVICE_INSTANCE_ID);
    }

    @Test
    void exportConsentsByTpp_failure_nullTppAuthorisationNumber() {
        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByTpp(null, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, psuIdData, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertTrue(aisConsents.isEmpty());
        verify(aisConsentSpecification, never())
            .byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(any(), any(), any(), any(), any());
    }

    @Test
    void exportConsentsByPsu_success() {
        // Given
        ConsentEntity consentEntity = buildConsentEntity();

        when(aisConsentSpecification.byPsuIdDataAndCreationPeriodAndInstanceId(psuIdData,
                                                                               CREATION_DATE_FROM,
                                                                               CREATION_DATE_TO,
                                                                               DEFAULT_SERVICE_INSTANCE_ID
        )).thenReturn((root, criteriaQuery, criteriaBuilder) -> null);
        when(consentJpaRepository.findAll(any())).thenReturn(Collections.singletonList(consentEntity));
        CmsAisAccountConsent expectedConsent = buildAisAccountConsent();

        List<AuthorisationEntity> authorisations = Collections.singletonList(new AuthorisationEntity());
        when(authorisationRepository.findAllByParentExternalIdAndType(EXTERNAL_CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(authorisations);
        when(aisConsentMapper.mapToCmsAisAccountConsent(consentEntity, authorisations))
            .thenReturn(buildAisAccountConsent());
        when(aisConsentLazyMigrationService.migrateIfNeeded(consentEntity))
            .thenReturn(consentEntity);

        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByPsu(psuIdData, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertFalse(aisConsents.isEmpty());
        assertTrue(aisConsents.contains(expectedConsent));
        verify(aisConsentSpecification, times(1))
            .byPsuIdDataAndCreationPeriodAndInstanceId(psuIdData, CREATION_DATE_FROM, CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID);
    }


    @Test
    void exportConsentsByPsu_successPagination() {
        // Given
        ConsentEntity consentEntity = buildConsentEntity();

        when(aisConsentSpecification.byPsuIdDataAndCreationPeriodAndInstanceId(psuIdData,
                                                                               CREATION_DATE_FROM,
                                                                               CREATION_DATE_TO,
                                                                               DEFAULT_SERVICE_INSTANCE_ID
        )).thenReturn((root, criteriaQuery, criteriaBuilder) -> null);
        PageRequest pageRequest = PageRequest.of(PAGE_INDEX, ITEMS_PER_PAGE);
        when(pageRequestBuilder.getPageParams(PAGE_INDEX, ITEMS_PER_PAGE)).thenReturn(pageRequest);

        when(consentJpaRepository.findAll(any(), eq(pageRequest))).thenReturn(new PageImpl<>(Collections.singletonList(consentEntity)));
        CmsAisAccountConsent expectedConsent = buildAisAccountConsent();

        List<AuthorisationEntity> authorisations = Collections.singletonList(new AuthorisationEntity());
        when(authorisationRepository.findAllByParentExternalIdAndType(EXTERNAL_CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(authorisations);
        when(aisConsentMapper.mapToCmsAisAccountConsent(consentEntity, authorisations))
            .thenReturn(buildAisAccountConsent());
        when(aisConsentLazyMigrationService.migrateIfNeeded(consentEntity))
            .thenReturn(consentEntity);

        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByPsu(psuIdData, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, PAGE_INDEX, ITEMS_PER_PAGE);

        // Then
        assertFalse(aisConsents.isEmpty());
        assertTrue(aisConsents.contains(expectedConsent));
        verify(aisConsentSpecification, times(1))
            .byPsuIdDataAndCreationPeriodAndInstanceId(psuIdData, CREATION_DATE_FROM, CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID);
    }

    @Test
    void exportConsentsByPsu_failure_wrongPsuIdData() {
        // Given
        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByPsu(wrongPsuIdData, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertTrue(aisConsents.isEmpty());
        verify(aisConsentSpecification, times(1))
            .byPsuIdDataAndCreationPeriodAndInstanceId(wrongPsuIdData, CREATION_DATE_FROM, CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID);
    }

    @Test
    void exportConsentsByPsu_failure_nullPsuIdData() {
        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByPsu(null, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertTrue(aisConsents.isEmpty());
        verify(aisConsentSpecification, never())
            .byPsuIdDataAndCreationPeriodAndInstanceId(any(), any(), any(), any());
    }

    @Test
    void exportConsentsByPsu_failure_emptyPsuIdData() {
        // Given
        PsuIdData emptyPsuIdData = new PsuIdData(null, null, null, null, null);

        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByPsu(emptyPsuIdData, CREATION_DATE_FROM,
                                                                 CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertTrue(aisConsents.isEmpty());
        verify(aisConsentSpecification, never()).byPsuIdDataAndCreationPeriodAndInstanceId(any(), any(), any(), any());
    }

    @Test
    void exportConsentsByAccountId_success() {
        // Given
        when(aisConsentSpecification.byAspspAccountIdAndCreationPeriodAndInstanceId(ASPSP_ACCOUNT_ID, CREATION_DATE_FROM,
                                                                                    CREATION_DATE_TO,
                                                                                    DEFAULT_SERVICE_INSTANCE_ID
        )).thenReturn((root, criteriaQuery, criteriaBuilder) -> null);
        ConsentEntity consentEntity = buildConsentEntity();
        when(consentJpaRepository.findAll(any()))
            .thenReturn(Collections.singletonList(consentEntity));
        List<AuthorisationEntity> authorisations = Collections.singletonList(new AuthorisationEntity());
        when(authorisationRepository.findAllByParentExternalIdAndType(EXTERNAL_CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(authorisations);
        when(aisConsentLazyMigrationService.migrateIfNeeded(consentEntity))
            .thenReturn(consentEntity);

        when(aisConsentMapper.mapToCmsAisAccountConsent(consentEntity, authorisations))
            .thenReturn(buildAisAccountConsent());
        CmsAisAccountConsent expectedConsent = buildAisAccountConsent();

        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByAccountId(ASPSP_ACCOUNT_ID, CREATION_DATE_FROM,
                                                                       CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertFalse(aisConsents.isEmpty());
        assertTrue(aisConsents.contains(expectedConsent));
        verify(aisConsentSpecification, times(1))
            .byAspspAccountIdAndCreationPeriodAndInstanceId(ASPSP_ACCOUNT_ID, CREATION_DATE_FROM, CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID);
    }

    @Test
    void exportConsentsByAccountId_successPagination() {
        // Given
        when(aisConsentSpecification.byAspspAccountIdAndCreationPeriodAndInstanceId(ASPSP_ACCOUNT_ID, CREATION_DATE_FROM,
                                                                                    CREATION_DATE_TO,
                                                                                    DEFAULT_SERVICE_INSTANCE_ID
        )).thenReturn((root, criteriaQuery, criteriaBuilder) -> null);
        ConsentEntity consentEntity = buildConsentEntity();
        PageRequest pageRequest = PageRequest.of(PAGE_INDEX, ITEMS_PER_PAGE);
        when(pageRequestBuilder.getPageParams(PAGE_INDEX, ITEMS_PER_PAGE)).thenReturn(pageRequest);
        when(consentJpaRepository.findAll(any(), eq(pageRequest)))
            .thenReturn(new PageImpl<>(Collections.singletonList(consentEntity)));
        List<AuthorisationEntity> authorisations = Collections.singletonList(new AuthorisationEntity());
        when(authorisationRepository.findAllByParentExternalIdAndType(EXTERNAL_CONSENT_ID, AuthorisationType.CONSENT))
            .thenReturn(authorisations);
        when(aisConsentLazyMigrationService.migrateIfNeeded(consentEntity))
            .thenReturn(consentEntity);

        when(aisConsentMapper.mapToCmsAisAccountConsent(consentEntity, authorisations))
            .thenReturn(buildAisAccountConsent());
        CmsAisAccountConsent expectedConsent = buildAisAccountConsent();

        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByAccountId(ASPSP_ACCOUNT_ID, CREATION_DATE_FROM,
                                                                       CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, PAGE_INDEX, ITEMS_PER_PAGE);

        // Then
        assertFalse(aisConsents.isEmpty());
        assertTrue(aisConsents.contains(expectedConsent));
        verify(aisConsentSpecification, times(1))
            .byAspspAccountIdAndCreationPeriodAndInstanceId(ASPSP_ACCOUNT_ID, CREATION_DATE_FROM, CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID);
    }

    @Test
    void exportConsentsByAccountId_success_withNoInstanceId() {
        Collection<CmsAisAccountConsent> cmsAisAccountConsents = cmsAspspAisExportServiceInternal.exportConsentsByAccountId(ASPSP_ACCOUNT_ID, CREATION_DATE_FROM, CREATION_DATE_TO, "", null, null);

        assertEquals(Collections.emptyList(), cmsAisAccountConsents);
    }

    @Test
    void exportConsentsByAccountId_failure_wrongAspspAccountId() {
        // When
        Collection<CmsAisAccountConsent> aisConsents =
            cmsAspspAisExportServiceInternal.exportConsentsByAccountId(WRONG_ASPSP_ACCOUNT_ID, CREATION_DATE_FROM,
                                                                       CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID, null, null);

        // Then
        assertTrue(aisConsents.isEmpty());
        verify(aisConsentSpecification, times(1))
            .byAspspAccountIdAndCreationPeriodAndInstanceId(WRONG_ASPSP_ACCOUNT_ID, CREATION_DATE_FROM, CREATION_DATE_TO, DEFAULT_SERVICE_INSTANCE_ID);
    }

    private PsuIdData buildPsuIdData(String psuId) {
        return new PsuIdData(psuId, null, null, null, null);
    }

    private CmsAisAccountConsent buildAisAccountConsent() {
        return new CmsAisAccountConsent(EXTERNAL_CONSENT_ID,
                                        null, false,
                                        null, null, 0,
                                        null, null,
                                        false, false, null, null, null, null, false, Collections.emptyList(), Collections.emptyMap(), CREATION_DATE_TIME, STATUS_CHANGE_DATE_TIME, null);
    }

    private ConsentEntity buildConsentEntity() {
        return jsonReader.getObjectFromFile("json/consent-entity.json", ConsentEntity.class);
    }
}
