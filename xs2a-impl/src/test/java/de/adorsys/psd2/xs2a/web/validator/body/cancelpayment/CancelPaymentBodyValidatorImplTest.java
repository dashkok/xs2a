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

package de.adorsys.psd2.xs2a.web.validator.body.cancelpayment;

import de.adorsys.psd2.xs2a.core.error.MessageError;
import de.adorsys.psd2.xs2a.web.validator.body.TppRedirectUriBodyValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelPaymentBodyValidatorImplTest {
    @InjectMocks
    private CancelPaymentBodyValidatorImpl cancelPaymentBodyValidator;

    @Mock
    private TppRedirectUriBodyValidatorImpl tppRedirectUriBodyValidator;

    private MessageError messageError;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        messageError = new MessageError();
        request = new MockHttpServletRequest();
    }

    @Test
    void validate() {
        cancelPaymentBodyValidator.validate(request, messageError);
        verify(tppRedirectUriBodyValidator).validate(request, messageError);
    }
}
