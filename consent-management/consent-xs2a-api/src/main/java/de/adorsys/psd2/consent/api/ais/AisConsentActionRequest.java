/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
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

package de.adorsys.psd2.consent.api.ais;

import de.adorsys.psd2.consent.api.ActionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AisConsentActionRequest {
    private String tppId;
    private String consentId;
    private ActionStatus actionStatus;
    private String requestUri;
    private boolean updateUsage;

    // These 2 parameters are optional. Are used while storing data about consent usage in case or request URI has resource
    // ID and/or transaction ID in path parameters.
    private String resourceId;
    private String transactionId;
}