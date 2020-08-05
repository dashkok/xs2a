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

package de.adorsys.psd2.xs2a.service.validator.piis;

import de.adorsys.psd2.core.data.piis.v1.PiisConsent;
import de.adorsys.psd2.xs2a.core.authorisation.ConsentAuthorization;
import de.adorsys.psd2.xs2a.core.error.ErrorType;
import de.adorsys.psd2.xs2a.service.validator.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static de.adorsys.psd2.xs2a.core.error.MessageErrorCode.RESOURCE_UNKNOWN_403;

@Slf4j
@Component
public class PiisAuthorisationValidator {

    @NotNull
    public ValidationResult validate(@NotNull String authorisationId, @NotNull PiisConsent consent) {
        Optional<ConsentAuthorization> authorisationOptional = consent.findAuthorisationInConsent(authorisationId);
        if (authorisationOptional.isEmpty()) {
            log.info("Consent ID: [{}], Authorisation ID: [{}]. Authorisation validation has failed: couldn't find authorisation with given authorisationId for consent",
                     consent.getId(), authorisationId);
            return ValidationResult.invalid(ErrorType.PIIS_403, RESOURCE_UNKNOWN_403);
        }

        return ValidationResult.valid();
    }
}
