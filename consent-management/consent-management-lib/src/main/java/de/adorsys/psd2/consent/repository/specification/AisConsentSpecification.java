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

package de.adorsys.psd2.consent.repository.specification;

import de.adorsys.psd2.consent.domain.consent.ConsentEntity;
import de.adorsys.psd2.xs2a.core.consent.ConsentType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AisConsentSpecification extends ConsentFilterableSpecification {
    public AisConsentSpecification(CommonSpecification<ConsentEntity> commonSpecification, ConsentSpecification consentSpecification) {
        super(commonSpecification, consentSpecification);
    }

    @Override
    public List<ConsentType> getTypes() {
        return Collections.singletonList(ConsentType.AIS);
    }
}
