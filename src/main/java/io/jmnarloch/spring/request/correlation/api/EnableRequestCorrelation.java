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
package io.jmnarloch.spring.request.correlation.api;

import io.jmnarloch.spring.request.correlation.client.RestTemplateCorrelationConfiguration;
import io.jmnarloch.spring.request.correlation.feign.FeignCorrelationConfiguration;
import io.jmnarloch.spring.request.correlation.support.RequestCorrelationConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.*;

/**
 * Enables automatic request correlation by assigning per each request unique identifier that afterwards is being
 * propagated through 'X-Request-Id' header.
 *
 * By default the identifier will be generated using random {@code UUID}.
 *
 * The header will be automatically propagated through any Spring configured {@link RestTemplate} bean or Feign client.
 *
 * @author Jakub Narloch
 * @see RequestCorrelation
 * @see RequestCorrelationConfiguration
 * @see CorrelationIdGenerator
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        RequestCorrelationConfiguration.class,
        RestTemplateCorrelationConfiguration.class,
        FeignCorrelationConfiguration.class
})
public @interface EnableRequestCorrelation {

}
