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

package de.adorsys.psd2.consent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PisPaymentRemoteUrls {
    @Value("${xs2a.cms.consent-service.baseurl:http://localhost:38080/api/v1}")
    private String paymentServiceBaseUrl;

    /**
     * Returns URL-string to CMS endpoint that updates payment status
     *
     * @return String
     */
    public String updatePaymentStatus() {
        return paymentServiceBaseUrl + "/pis/payment/{payment-id}/status/{status}";
    }

    public String updateInternalPaymentStatus() {
        return paymentServiceBaseUrl + "/pis/payment/{payment-id}/internal-status/{status}";
    }

    public String updatePaymentCancellationRedirectURIs() {
        return paymentServiceBaseUrl + "/pis/payment/{payment-id}/cancellation/redirects";
    }

    public String updatePaymentCancellationInternalRequestId() {
        return paymentServiceBaseUrl + "/pis/payment/{payment-id}/cancellation/internal-request-id/{internal-request-id}";
    }
}
