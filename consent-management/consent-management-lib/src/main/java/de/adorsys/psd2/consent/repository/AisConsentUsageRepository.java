/*
 * Copyright 2018-2019 adorsys GmbH & Co KG
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

package de.adorsys.psd2.consent.repository;

import de.adorsys.psd2.consent.domain.account.AisConsentUsage;
import de.adorsys.psd2.consent.domain.consent.ConsentEntity;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AisConsentUsageRepository extends CrudRepository<AisConsentUsage, Long> {
    @Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<AisConsentUsage> findWriteByConsentAndUsageDateAndRequestUri(ConsentEntity aisConsent, LocalDate usageDate, String requestUri);

    @Lock(value = LockModeType.OPTIMISTIC)
    List<AisConsentUsage> findReadByConsentAndUsageDate(ConsentEntity aisConsent, LocalDate usageDate);

    int countByConsentIdAndResourceId(Long consentId, String resourceId);

    int countByConsentIdAndRequestUri(Long consentId, String requestUri);
}
