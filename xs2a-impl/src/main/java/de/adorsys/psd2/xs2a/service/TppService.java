/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
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

import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.exception.CertificateException;
import de.adorsys.psd2.xs2a.service.validator.tpp.TppInfoHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TppService {
    private final TppInfoHolder tppInfoHolder;

    public String getTppId() {
        return Optional.ofNullable(tppInfoHolder.getTppInfo())
                   .map(TppInfo::getAuthorisationNumber)
                   .orElseThrow(() -> {
                       log.info("Can't get TPP id. Please check TPP Certificate");
                       return new CertificateException();
                   });
    }

    public TppInfo getTppInfo() {
        return Optional.ofNullable(tppInfoHolder.getTppInfo())
                   .orElseThrow(() -> {
                       log.info("Can't get TppInfo. Please check TPP Certificate");
                       return new CertificateException();
                   });
    }
}