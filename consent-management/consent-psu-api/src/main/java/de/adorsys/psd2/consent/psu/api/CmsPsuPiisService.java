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

package de.adorsys.psd2.consent.psu.api;

import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;


public interface CmsPsuPiisService {
    /**
     * Returns PIIS Consent object by its ID
     *
     * @param psuIdData     PSU credentials data
     * @param consentId     ID of Consent
     * @param instanceId    optional ID of particular service instance
     * @return Consent object if it was found and it corresponds to the user data given in parameter
     */
    @NotNull
    Optional<CmsPiisConsent> getConsent(@NotNull PsuIdData psuIdData, @NotNull String consentId, @NotNull String instanceId);

    /**
     * Returns a list of PIIS Consent objects by PSU ID
     *
     * @param psuIdData     PSU credentials data
     * @param instanceId    optional ID of particular service instance
     * @param pageIndex index of current page
     * @param itemsPerPage quantity of consents on one page
     * @return List of PIIS Consent objects corresponding to the given PSU
     */
    @NotNull
    List<CmsPiisConsent> getConsentsForPsu(@NotNull PsuIdData psuIdData, @NotNull String instanceId, Integer pageIndex, Integer itemsPerPage);

    /**
     * Revokes PIIS Consent object by its ID. Consent gets status "Revoked by PSU".
     *
     * @param psuIdData     PSU credentials data
     * @param consentId     ID of Consent
     * @param instanceId    optional ID of particular service instance
     * @return <code>true</code> if consent was found and revoked. <code>false</code> otherwise.
     */
    boolean revokeConsent(@NotNull PsuIdData psuIdData, @NotNull String consentId, @NotNull String instanceId);
}
