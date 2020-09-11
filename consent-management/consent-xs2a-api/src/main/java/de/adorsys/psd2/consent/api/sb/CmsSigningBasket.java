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

package de.adorsys.psd2.consent.api.sb;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.consent.api.pis.PisCommonPaymentResponse;
import de.adorsys.psd2.xs2a.core.authorisation.AuthorisationTemplate;
import de.adorsys.psd2.xs2a.core.sb.SigningBasketTppInformation;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sb.SigningBasketTransactionStatus;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CmsSigningBasket {
    private String id;
    private String instanceId;
    private List<CmsConsent> consents;
    private List<PisCommonPaymentResponse> payments;
    private AuthorisationTemplate authorisationTemplate;
    private SigningBasketTransactionStatus transactionStatus;
    private String internalRequestId;
    private List<PsuIdData> psuIdDatas;
    private boolean multilevelScaRequired;
    private SigningBasketTppInformation tppInformation;
}