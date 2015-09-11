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
package io.jmnarloch.spring.request.correlation.support;

import io.jmnarloch.spring.request.correlation.api.CorrelationIdGenerator;
import io.jmnarloch.spring.request.correlation.api.RequestCorrelationInterceptor;
import io.jmnarloch.spring.request.correlation.filter.RequestCorrelationFilter;
import io.jmnarloch.spring.request.correlation.generator.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Configures the request correlation filter.
 *
 * @author Jakub Narloch
 * @see io.jmnarloch.spring.request.correlation.api.EnableRequestCorrelation
 */
@Configuration
public class RequestCorrelationConfiguration {

    @Autowired(required = false)
    private List<RequestCorrelationInterceptor> interceptors = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean(CorrelationIdGenerator.class)
    public CorrelationIdGenerator requestIdGenerator() {
        return new UuidGenerator();
    }

    @Bean
    public RequestCorrelationFilter requestCorrelationFilter(CorrelationIdGenerator generator) {

        return new RequestCorrelationFilter(generator, interceptors);
    }

    @Bean
    public FilterRegistrationBean requestCorrelationFilterBean(RequestCorrelationFilter correlationFilter) {

        final FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(correlationFilter);
        filterRegistration.setMatchAfter(false);
        filterRegistration.setDispatcherTypes(
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC));
        filterRegistration.setAsyncSupported(true);
        filterRegistration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistration;
    }
}
