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

package de.adorsys.psd2.consent.api.service;

import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.signingBasket.*;

/**
 * Base version of ConsentService that contains all method declarations.
 * Should not be implemented directly, consider using one of the interfaces that extends this one.
 *
 * @see ConsentService
 * @see ConsentServiceEncrypted
 */
interface SigningBasketServiceBase {

    /**
     * Creates Signing Basket
     *
     * @param signingBasket needed parameters for creating Signing Basket
     * @return create sigining basket response, containing basket and its encrypted ID
     */
    CmsResponse<CmsCreateSigningBasketResponse> createSigningBasket(CmsSigningBasket signingBasket);
}
