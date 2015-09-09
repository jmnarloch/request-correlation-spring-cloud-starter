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
package com.github.jmnarloch.spring.request.correletion.support;

import com.github.jmnarloch.spring.request.correletion.api.RequestIdGenerator;
import com.github.jmnarloch.spring.request.correletion.filter.RequestCorrelationFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 *
 */
@Configuration
public class RequestCorrelationAutoConfiguration {

    @Bean
    public RequestCorrelationFilter requestCorrelationFilter(RequestIdGenerator generator) {

        return new RequestCorrelationFilter(generator);
    }

    @Bean
    public FilterRegistrationBean requestCorrelationFilterBean(RequestCorrelationFilter correlationFilter) {

        final FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(correlationFilter);
        filterRegistration.setMatchAfter(false);
        filterRegistration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        filterRegistration.setAsyncSupported(true);
        filterRegistration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistration;
    }
}
