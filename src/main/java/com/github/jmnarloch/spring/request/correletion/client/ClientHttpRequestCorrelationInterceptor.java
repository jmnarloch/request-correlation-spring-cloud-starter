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
package com.github.jmnarloch.spring.request.correletion.client;

import com.github.jmnarloch.spring.request.correletion.support.RequestCorrelationConsts;
import com.github.jmnarloch.spring.request.correletion.support.RequestCorrelationUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 *
 */
public class ClientHttpRequestCorrelationInterceptor implements ClientHttpRequestInterceptor {

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
