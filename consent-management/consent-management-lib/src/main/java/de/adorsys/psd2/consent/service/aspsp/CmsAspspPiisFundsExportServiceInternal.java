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

import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import de.adorsys.psd2.consent.aspsp.api.piis.CmsAspspPiisFundsExportService;
import de.adorsys.psd2.consent.domain.consent.ConsentEntity;
import de.adorsys.psd2.consent.repository.ConsentJpaRepository;
import de.adorsys.psd2.consent.repository.specification.PiisConsentEntitySpecification;
import de.adorsys.psd2.consent.service.mapper.PiisConsentMapper;
import de.adorsys.psd2.consent.service.migration.PiisConsentLazyMigrationService;
import de.adorsys.psd2.consent.service.psu.util.PageRequestBuilder;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmsAspspPiisFundsExportServiceInternal implements CmsAspspPiisFundsExportService {
    private static final String DEFAULT_SERVICE_INSTANCE_ID = "UNDEFINED";

    private final ConsentJpaRepository consentJpaRepository;
    private final PiisConsentEntitySpecification piisConsentEntitySpecification;
    private final PiisConsentMapper piisConsentMapper;
    private final PiisConsentLazyMigrationService piisConsentLazyMigrationService;
    private final PageRequestBuilder pageRequestBuilder;

    @Override
    @Transactional
    public Collection<CmsPiisConsent> exportConsentsByTpp(String tppAuthorisationNumber,
                                                          @Nullable LocalDate createDateFrom,
                                                          @Nullable LocalDate createDateTo, @Nullable PsuIdData psuIdData,
                                                          @Nullable String instanceId,
                                                          Integer pageIndex, Integer itemsPerPage) {
        if (StringUtils.isBlank(tppAuthorisationNumber)) {
            log.info("TPP ID: [{}], instanceId: [{}]. Export consents by TPP failed, TPP ID is empty or null.",
                     tppAuthorisationNumber, instanceId);
            return Collections.emptyList();
        }

        String actualInstanceId = StringUtils.defaultIfEmpty(instanceId, DEFAULT_SERVICE_INSTANCE_ID);

        if (pageIndex == null && itemsPerPage == null) {
            return findAllBySpecification(piisConsentEntitySpecification.byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(tppAuthorisationNumber, createDateFrom, createDateTo, psuIdData, actualInstanceId));
        } else {
            PageRequest pageRequest = pageRequestBuilder.getPageParams(pageIndex, itemsPerPage);
            return findAllBySpecification(piisConsentEntitySpecification.byTppIdAndCreationPeriodAndPsuIdDataAndInstanceId(tppAuthorisationNumber, createDateFrom, createDateTo, psuIdData, actualInstanceId), pageRequest);
        }
    }

    @Override
    @Transactional
    public Collection<CmsPiisConsent> exportConsentsByPsu(PsuIdData psuIdData, @Nullable LocalDate createDateFrom,
                                                          @Nullable LocalDate createDateTo, @Nullable String instanceId,
                                                          Integer pageIndex, Integer itemsPerPage) {
        if (psuIdData == null || psuIdData.isEmpty()) {
            log.info("InstanceId: [{}]. Export consents by psu failed, psuIdData is empty or null.", instanceId);
            return Collections.emptyList();
        }

        String actualInstanceId = StringUtils.defaultIfEmpty(instanceId, DEFAULT_SERVICE_INSTANCE_ID);

        if (pageIndex == null && itemsPerPage == null) {
            return findAllBySpecification(piisConsentEntitySpecification.byPsuIdDataAndCreationPeriodAndInstanceId(psuIdData, createDateFrom, createDateTo, actualInstanceId));
        } else {
            PageRequest pageRequest = pageRequestBuilder.getPageParams(pageIndex, itemsPerPage);
            return findAllBySpecification(piisConsentEntitySpecification.byPsuIdDataAndCreationPeriodAndInstanceId(psuIdData, createDateFrom, createDateTo, actualInstanceId), pageRequest);
        }
    }

    @Override
    @Transactional
    public Collection<CmsPiisConsent> exportConsentsByAccountId(@NotNull String aspspAccountId,
                                                                @Nullable LocalDate createDateFrom,
                                                                @Nullable LocalDate createDateTo,
                                                                @Nullable String instanceId,
                                                                Integer pageIndex, Integer itemsPerPage) {
        if (StringUtils.isBlank(aspspAccountId)) {
            log.info("InstanceId: [{}]. Export consents by accountId failed, aspspAccountId is empty or null.", instanceId);
            return Collections.emptyList();
        }

        String actualInstanceId = StringUtils.defaultIfEmpty(instanceId, DEFAULT_SERVICE_INSTANCE_ID);
        if (pageIndex == null && itemsPerPage == null) {
            return findAllBySpecification(piisConsentEntitySpecification.byAspspAccountIdAndCreationPeriodAndInstanceId(aspspAccountId, createDateFrom, createDateTo, actualInstanceId));
        } else {
            PageRequest pageRequest = pageRequestBuilder.getPageParams(pageIndex, itemsPerPage);
            return findAllBySpecification(piisConsentEntitySpecification.byAspspAccountIdAndCreationPeriodAndInstanceId(aspspAccountId, createDateFrom, createDateTo, actualInstanceId), pageRequest);
        }
    }

    private Collection<CmsPiisConsent> findAllBySpecification(Specification<ConsentEntity> specification) {
        List<ConsentEntity> piisConsentEntities = consentJpaRepository.findAll(specification);
        piisConsentLazyMigrationService.migrateIfNeeded(piisConsentEntities);
        return piisConsentMapper.mapToCmsPiisConsentList(piisConsentEntities);
    }

    private Collection<CmsPiisConsent> findAllBySpecification(Specification<ConsentEntity> specification, Pageable pageable) {
        List<ConsentEntity> piisConsentEntities = consentJpaRepository.findAll(specification, pageable)
                                                      .stream()
                                                      .collect(Collectors.toList());
        piisConsentLazyMigrationService.migrateIfNeeded(piisConsentEntities);
        return piisConsentMapper.mapToCmsPiisConsentList(piisConsentEntities);
    }
}
