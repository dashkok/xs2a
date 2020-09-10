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

package de.adorsys.psd2.xs2a.web.header;

import de.adorsys.psd2.xs2a.domain.NotificationModeResponseHeaders;
import de.adorsys.psd2.xs2a.service.ScaApproachResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SigningBasketHeadersBuilder extends AbstractHeadersBuilder {

    @Autowired
    public SigningBasketHeadersBuilder(ScaApproachResolver scaApproachResolver) {
        super(scaApproachResolver);
    }

    public ResponseHeaders buildCreateSigningBasketHeaders(@NotNull String selfLink, @NotNull NotificationModeResponseHeaders notificationHeaders) {
        ResponseHeaders.ResponseHeadersBuilder responseHeadersBuilder = ResponseHeaders.builder();
        buildNotificationHeaders(responseHeadersBuilder, notificationHeaders);
        buildCreateSigningBasketHeaders(responseHeadersBuilder, selfLink);
        return responseHeadersBuilder.build();
    }

    public ResponseHeaders buildCreateSigningBasketHeaders(@NotNull String selfLink) {
        ResponseHeaders.ResponseHeadersBuilder responseHeadersBuilder = ResponseHeaders.builder();
        buildCreateSigningBasketHeaders(responseHeadersBuilder, selfLink);
        return responseHeadersBuilder.build();
    }

    private void buildNotificationHeaders(ResponseHeaders.ResponseHeadersBuilder builder, @NotNull NotificationModeResponseHeaders notificationHeaders) {
        builder.notificationSupport(notificationHeaders.getAspspNotificationSupport());
        builder.notificationContent(notificationHeaders.getAspspNotificationContent());
    }

    private void buildCreateSigningBasketHeaders(ResponseHeaders.ResponseHeadersBuilder builder, @NotNull String selfLink) {
        builder.location(selfLink);
    }
}
