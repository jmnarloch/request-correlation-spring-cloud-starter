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
package io.jmnarloch.spring.request.correlation.http;

import io.jmnarloch.spring.request.correlation.support.RequestCorrelationConsts;
import io.jmnarloch.spring.request.correlation.support.RequestCorrelationProperties;
import io.jmnarloch.spring.request.correlation.support.RequestCorrelationUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Rest template http interceptor, that propagates the currents thread bound request identifier to the outgoing request,
 * through 'X-Request-Id' header.
 *
 * @author Jakub Narloch
 */
public class ClientHttpRequestCorrelationInterceptor implements ClientHttpRequestInterceptor {

    /**
     * The correlation properties.
     */
    private final RequestCorrelationProperties properties;

    /**
     * Creates new instance of {@link ClientHttpRequestCorrelationInterceptor}.
     *
     * @param properties the properties
     * @throws IllegalArgumentException if {@code properties} is {@code null}
     */
    public ClientHttpRequestCorrelationInterceptor(RequestCorrelationProperties properties) {
        Assert.notNull(properties, "Parameter 'properties' can not be null");

        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // sets the correlation id
        final String correlationId = RequestCorrelationUtils.getCurrentCorrelationId();
        if(correlationId != null) {
            request.getHeaders().add(RequestCorrelationConsts.HEADER_NAME, correlationId);
        }

        // proceeds with execution
        return execution.execute(request, body);
    }
}
