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

package de.adorsys.psd2.xs2a.web.validator;

import de.adorsys.psd2.xs2a.web.validator.body.consent.TransactionListBodyValidator;
import de.adorsys.psd2.xs2a.web.validator.header.account.TransactionListHeaderValidator;
import de.adorsys.psd2.xs2a.web.validator.query.account.TransactionListQueryParamsValidator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionListMethodValidatorImpl extends AbstractMethodValidator {
    private static final String METHOD_NAME = "_getTransactionList";

    protected TransactionListMethodValidatorImpl(List<TransactionListHeaderValidator> headerValidators,
                                                 List<TransactionListBodyValidator> bodyValidators,
                                                 List<TransactionListQueryParamsValidator> queryParamsValidator) {
        super(ValidatorWrapper.builder()
                  .headerValidators(headerValidators)
                  .bodyValidators(bodyValidators)
                  .queryParameterValidators(queryParamsValidator)
                  .build());
    }

    @Override
    public String getMethodName() {
        return METHOD_NAME;
    }
}