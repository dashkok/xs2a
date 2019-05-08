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

package de.adorsys.psd2.xs2a.web.header;

import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import de.adorsys.psd2.xs2a.service.ScaApproachResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentCancellationHeadersBuilderTest {
    private static final String AUTHORISATION_ID = "123";
    private static final ScaApproach SCA_APPROACH = ScaApproach.REDIRECT;
    private static final String ASPSP_SCA_APPROACH_HEADER = "Aspsp-Sca-Approach";

    @Mock
    private ScaApproachResolver scaApproachResolver;
    @InjectMocks
    private PaymentCancellationHeadersBuilder paymentCancellationHeadersBuilder;

    @Test
    public void buildStartPaymentCancellationAuthorisationHeaders_shouldReturnScaApproachFromAuthorisation() {
        // Given
        when(scaApproachResolver.getCancellationScaApproach(AUTHORISATION_ID))
            .thenReturn(SCA_APPROACH);

        HttpHeaders expectedHttpHeaders = new HttpHeaders();
        expectedHttpHeaders.add(ASPSP_SCA_APPROACH_HEADER, SCA_APPROACH.name());

        // When
        ResponseHeaders responseHeaders = paymentCancellationHeadersBuilder.buildStartPaymentCancellationAuthorisationHeaders(AUTHORISATION_ID);

        // Then
        verify(scaApproachResolver, never()).resolveScaApproach();

        HttpHeaders actualHttpHeaders = responseHeaders.getHttpHeaders();
        assertEquals(expectedHttpHeaders, actualHttpHeaders);
    }

    @Test
    public void buildErrorStartPaymentCancellationAuthorisationHeaders_shouldReturnResolvedScaApproach() {
        // Given
        when(scaApproachResolver.resolveScaApproach())
            .thenReturn(SCA_APPROACH);

        HttpHeaders expectedHttpHeaders = new HttpHeaders();
        expectedHttpHeaders.add(ASPSP_SCA_APPROACH_HEADER, SCA_APPROACH.name());

        // When
        ResponseHeaders responseHeaders = paymentCancellationHeadersBuilder.buildErrorStartPaymentCancellationAuthorisationHeaders();

        // Then
        verify(scaApproachResolver, never()).getCancellationScaApproach(any());

        HttpHeaders actualHttpHeaders = responseHeaders.getHttpHeaders();
        assertEquals(expectedHttpHeaders, actualHttpHeaders);
    }

    @Test
    public void buildUpdatePaymentCancellationPsuDataHeaders_shouldReturnScaApproachFromAuthorisation() {
        // Given
        when(scaApproachResolver.getCancellationScaApproach(AUTHORISATION_ID))
            .thenReturn(SCA_APPROACH);

        HttpHeaders expectedHttpHeaders = new HttpHeaders();
        expectedHttpHeaders.add(ASPSP_SCA_APPROACH_HEADER, SCA_APPROACH.name());

        // When
        ResponseHeaders responseHeaders = paymentCancellationHeadersBuilder.buildUpdatePaymentCancellationPsuDataHeaders(AUTHORISATION_ID);

        // Then
        verify(scaApproachResolver, never()).resolveScaApproach();

        HttpHeaders actualHttpHeaders = responseHeaders.getHttpHeaders();
        assertEquals(expectedHttpHeaders, actualHttpHeaders);
    }

    @Test
    public void buildErrorUpdatePaymentCancellationPsuDataHeaders_shouldReturnScaApproachFromAuthorisation() {
        // Given
        when(scaApproachResolver.getCancellationScaApproach(AUTHORISATION_ID))
            .thenReturn(SCA_APPROACH);

        HttpHeaders expectedHttpHeaders = new HttpHeaders();
        expectedHttpHeaders.add(ASPSP_SCA_APPROACH_HEADER, SCA_APPROACH.name());

        // When
        ResponseHeaders responseHeaders = paymentCancellationHeadersBuilder.buildErrorUpdatePaymentCancellationPsuDataHeaders(AUTHORISATION_ID);

        // Then
        verify(scaApproachResolver, never()).resolveScaApproach();

        HttpHeaders actualHttpHeaders = responseHeaders.getHttpHeaders();
        assertEquals(expectedHttpHeaders, actualHttpHeaders);
    }
}
