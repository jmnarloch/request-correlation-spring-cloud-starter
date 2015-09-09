/**
 * Copyright (c) 2015 the original author or authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jmnarloch.spring.request.correletion.support;

import com.github.jmnarloch.spring.request.correletion.api.RequestCorrelation;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * A utility class for retrieving the currently bound request correlation id.
 *
 * @author Jakub Narloch
 */
public class RequestCorrelationUtils {

    /**
     * Retrieves the current request correlation id if present.
     *
     * @return the correlation id or {@code null}
     */
    @SuppressWarnings("unchecked")
    public static String getCurrentCorrelationId() {

        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            Object correlationId = requestAttributes
                    .getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);

            if (correlationId instanceof RequestCorrelation) {
                return ((RequestCorrelation) correlationId).getRequestId();
            }
        }
        return null;
    }
}
