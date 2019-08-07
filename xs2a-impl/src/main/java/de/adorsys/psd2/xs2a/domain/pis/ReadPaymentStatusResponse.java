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

package de.adorsys.psd2.xs2a.domain.pis;

import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.domain.ErrorHolder;
import lombok.Data;

@Data
public class ReadPaymentStatusResponse {
    private TransactionStatus status;
    private Boolean fundsAvailable;
    private ErrorHolder errorHolder;

    public ReadPaymentStatusResponse(TransactionStatus status, Boolean fundsAvailable) {
        this.status = status;
        this.fundsAvailable = fundsAvailable;
    }

    public ReadPaymentStatusResponse(ErrorHolder errorHolder) {
        this.errorHolder = errorHolder;
    }

    public boolean hasError() {
        return errorHolder != null;
    }
}
