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

package de.adorsys.psd2.xs2a.service;

import de.adorsys.psd2.core.data.AccountAccess;
import de.adorsys.psd2.xs2a.core.ais.AccountAccessType;
import de.adorsys.psd2.xs2a.core.profile.AdditionalInformationAccess;
import de.adorsys.psd2.xs2a.domain.consent.CreateConsentReq;
import de.adorsys.psd2.xs2a.service.profile.AspspProfileServiceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static de.adorsys.psd2.xs2a.core.ais.AccountAccessType.ALL_ACCOUNTS;
import static de.adorsys.psd2.xs2a.core.ais.AccountAccessType.ALL_ACCOUNTS_WITH_OWNER_NAME;

@Service
@RequiredArgsConstructor
public class AccountOwnerInformationService {
    private final AspspProfileServiceWrapper aspspProfileService;

    public CreateConsentReq checkSupportedAccountOwnerInformation(CreateConsentReq request) {
        if (!aspspProfileService.isAccountOwnerInformationSupported()) {
            return cleanAccountOwnerInformation(request);
        }
        return request;
    }

    private CreateConsentReq cleanAccountOwnerInformation(CreateConsentReq request) {
        AccountAccess access = request.getAccess();

        AccountAccessType allAccountsWithOwnerName = ALL_ACCOUNTS_WITH_OWNER_NAME;
        AccountAccessType allAccounts = ALL_ACCOUNTS;

        if (request.getAvailableAccounts() == allAccountsWithOwnerName) {
            request.setAvailableAccounts(allAccounts);
        }
        if (request.getAvailableAccountsWithBalance() == allAccountsWithOwnerName) {
            request.setAvailableAccountsWithBalance(allAccounts);
        }
        if (request.getAllPsd2() == allAccountsWithOwnerName) {
            request.setAllPsd2(allAccounts);
        }
        if (isConsentWithAdditionalInformationAccess(access)) {
            AccountAccess accessWithoutOwnerName = new AccountAccess(access.getAccounts(), access.getBalances(), access.getTransactions(), null);
            request.setAccess(accessWithoutOwnerName);
        }

        return request;
    }

    private boolean isConsentWithAdditionalInformationAccess(AccountAccess access) {
        return Optional.ofNullable(access.getAdditionalInformationAccess())
                   .map(AdditionalInformationAccess::getOwnerName)
                   .isPresent();
    }
}
