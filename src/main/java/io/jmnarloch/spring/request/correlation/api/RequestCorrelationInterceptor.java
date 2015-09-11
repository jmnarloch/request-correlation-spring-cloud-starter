/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.request.correlation.api;

/**
 * An interceptor that can be used for
 *
 * @author Jakub Narloch
 */
public interface RequestCorrelationInterceptor {

    /**
     * Callback method called whenever the correlation id has been assigned for the current request, no matter whether
     * it has set from the request header value or a new id has generated for incoming request.
     *
     * @param correlationId the correlation id
     */
    void afterCorrelationIdSet(String correlationId);
}
