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

package de.adorsys.psd2.consent.service.sha;


import de.adorsys.psd2.consent.service.sha.v3.AisChecksumCalculatingServiceV3;
import de.adorsys.psd2.xs2a.core.consent.ConsentType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChecksumCalculatingFactory {
    private MultiKeyMap<MultiKey, ChecksumCalculatingService> services = new MultiKeyMap();

    @Autowired
    private AisChecksumCalculatingServiceV3 aisV3;
    @Autowired
    private NoProcessingChecksumService  noProcessingService;

    @PostConstruct
    public void init() {
        // for deprecated services will use noProcessingService
        services.put(new MultiKey("001", ConsentType.AIS), noProcessingService);
        services.put(new MultiKey("002", ConsentType.AIS), noProcessingService);

        services.put(new MultiKey(aisV3.getVersion(), ConsentType.AIS), aisV3);
    }

    /** Provides an appropriate checksum calculator by checksum and consent type
     *
     * @param checksum Data with calculator version info
     * @param consentType Type of consent
     * @return Optional value  of  checksum calculating service
     */
    public Optional<ChecksumCalculatingService> getServiceByChecksum(byte[] checksum, ConsentType consentType) {
        if (checksum == null) {
            return getDefaultService(consentType);
        }

        String checksumStr = new String(checksum);
        String[] elements = checksumStr.split(ChecksumConstant.DELIMITER);

        if (elements.length < 1) {
            return getDefaultService(consentType);
        }

        String versionSting = elements[ChecksumConstant.VERSION_START_POSITION];

        return Optional.ofNullable(services.get(new MultiKey(versionSting, consentType)));
    }

    private Optional<ChecksumCalculatingService> getDefaultService(ConsentType consentType) {
        if (ConsentType.AIS == consentType) {
            return Optional.of(aisV3);
        }
        return Optional.empty();
    }
}
