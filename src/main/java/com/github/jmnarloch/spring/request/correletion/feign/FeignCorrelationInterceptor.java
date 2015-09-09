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
package com.github.jmnarloch.spring.request.correletion.feign;

import com.github.jmnarloch.spring.request.correletion.support.RequestCorrelationConsts;
import com.github.jmnarloch.spring.request.correletion.support.RequestCorrelationUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign request correlation interceptor.
 *
 * @author Jakub Narloch
 */
public class FeignCorrelationInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        final String correlationId = RequestCorrelationUtils.getCurrentCorrelationId();
        if(correlationId != null) {
            template.header(RequestCorrelationConsts.HEADER_NAME, correlationId);
        }
    }
}
