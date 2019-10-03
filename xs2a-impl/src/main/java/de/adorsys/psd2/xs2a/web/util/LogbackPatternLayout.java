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

package de.adorsys.psd2.xs2a.web.util;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class LogbackPatternLayout extends PatternLayout {
    private final Map<String, String> mapForReplacement = new HashMap<>();
    static final String mask = "*****";

    public LogbackPatternLayout() {
        String replaceField = "$1\"" + mask + "\"";
        String replaceHeader = "$1" + mask;
        UnaryOperator<String> replace = name -> "\"" + name + "\":\"" + mask + "\"";

        UnaryOperator<String> fieldRegex = name -> "(\"" + name + "\"\\s*:\\s*)\"[^\"]+\"";
        UnaryOperator<String> headerRegex = name -> "(" + name + "\\s*:\\s*)[^,]+";
        UnaryOperator<String> objectRegex = name -> "(\"" + name + "\"\\s*:\\s*\\{)[^}]+}";

        mapForReplacement.put(fieldRegex.apply("ownerName"), replaceField);
        mapForReplacement.put(fieldRegex.apply("\\w*[Pp]assword"), replaceField);
        mapForReplacement.put(fieldRegex.apply("access_token"), replaceField);
        mapForReplacement.put(fieldRegex.apply("refresh_token"), replaceField);
        mapForReplacement.put(headerRegex.apply("Authorization"), replaceHeader);
        mapForReplacement.put(objectRegex.apply("ownerAddress"), replace.apply("ownerAddress"));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return modifyMessage(super.doLayout(event));
    }

    String modifyMessage(String message) {
        AtomicReference<String> logMessage = new AtomicReference<>(message);
        mapForReplacement.forEach((key, value) -> logMessage.set(Pattern.compile(key, Pattern.CASE_INSENSITIVE)
                                                                     .matcher(logMessage.get())
                                                                     .replaceAll(value)));
        return logMessage.get();
    }
}

