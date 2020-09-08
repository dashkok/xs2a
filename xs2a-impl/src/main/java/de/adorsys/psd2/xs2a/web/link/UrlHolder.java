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

package de.adorsys.psd2.xs2a.web.link;

public class UrlHolder {
    // PAYMENT
    public static final String PAYMENT_LINK_URL = "/v1/{payment-service}/{payment-product}/{payment-id}";
    public static final String PAYMENT_STATUS_URL = "/v1/{payment-service}/{payment-product}/{payment-id}/status";
    public static final String START_PIS_AUTHORISATION_URL = "/v1/{payment-service}/{payment-product}/{payment-id}/authorisations";
    public static final String PIS_AUTHORISATION_LINK_URL = "/v1/{payment-service}/{payment-product}/{payment-id}/authorisations/{authorisation-id}";
    public static final String START_PIS_CANCELLATION_AUTH_URL = "/v1/{payment-service}/{payment-product}/{payment-id}/cancellation-authorisations";
    public static final String PIS_CANCELLATION_AUTH_LINK_URL = "/v1/{payment-service}/{payment-product}/{payment-id}/cancellation-authorisations/{authorisation-id}";

    // CONSENT
    public static final String CONSENT_LINK_URL = "/v1/consents/{consentId}";
    public static final String CONSENT_STATUS_URL = "/v1/consents/{consentId}/status";
    public static final String CREATE_AIS_AUTHORISATION_URL = "/v1/consents/{consentId}/authorisations";
    public static final String AIS_AUTHORISATION_URL = "/v1/consents/{consentId}/authorisations/{authorisation-id}";

    // ACCOUNT
    public static final String ACCOUNT_LINK_URL = "/v1/accounts/{accountId}";
    public static final String ACCOUNT_BALANCES_URL = "/v1/accounts/{accountId}/balances";
    public static final String ACCOUNT_TRANSACTIONS_URL = "/v1/accounts/{accountId}/transactions";
    public static final String ACCOUNT_TRANSACTIONS_DOWNLOAD_URL = "/v1/accounts/{accountId}/transactions/download/{downloadId}";

    // CARD
    public static final String CARD_LINK_URL = "/v1/card-accounts/{accountId}";
    public static final String CARD_BALANCES_URL = "/v1/card-accounts/{accountId}/balances";
    public static final String CARD_TRANSACTIONS_URL = "/v1/card-accounts/{accountId}/transactions";
    public static final String CARD_TRANSACTIONS_DOWNLOAD_URL = "/v1/card-accounts/{accountId}/transactions/download/{downloadId}";

    // PIIS CONSENT
    public static final String PIIS_PREFIX = "/v2/consents/confirmation-of-funds";
    public static final String PIIS_CONSENT_LINK_URL = PIIS_PREFIX + "/{consentId}";
    public static final String PIIS_CONSENT_STATUS_URL = PIIS_PREFIX + "/{consentId}/status";
    public static final String PIIS_AUTHORISATION_URL = PIIS_PREFIX + "/{consentId}/authorisations/{authorisation-id}";
    public static final String CREATE_PIIS_AUTHORISATION_URL = PIIS_PREFIX + "/{consentId}/authorisations";

    // SIGNING BASKET
    public static final String SIGNING_BASKET_LINK_URL = "/v1/signing-baskets/{basketId}";
    public static final String SIGNING_BASKET_STATUS_URL = "/v1/signing-baskets/{basketId}/status";
    public static final String CREATE_SIGNING_BASKET_AUTHORISATION_URL = "/v1/signing-baskets/{basketId}/authorisations";

    private UrlHolder() {
    }
}
