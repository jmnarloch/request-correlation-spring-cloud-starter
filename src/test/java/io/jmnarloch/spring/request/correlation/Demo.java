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
package io.jmnarloch.spring.request.correlation;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import io.jmnarloch.spring.request.correlation.api.EnableRequestCorrelation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;

/**
 * Demonstrates the usage of this component.
 *
 * @author Jakub Narloch
 */
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
@SpringApplicationConfiguration(classes = {Demo.Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class Demo {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test() {

        // when
        final String requestId = restTemplate.getForObject(url("/"), String.class);
        assertNotNull(requestId);
    }

    @Test
    public void testRestTemplate() {

        // when
        final String requestId = restTemplate.getForObject(url("/rest"), String.class);
        assertNotNull(requestId);
    }

    @Test
    public void testFeign() {

        // when
        final String requestId = restTemplate.getForObject(url("/feign"), String.class);
        assertNotNull(requestId);
    }

    private String url(String path) {

        return String.format("http://127.0.0.1:%d/%s", port, path);
    }

    @FeignClient("local")
    interface CorrelatedFeignClient {

        @RequestMapping(value = "/", method = RequestMethod.GET)
        String getRequestId();
    }

    @RestController
    @EnableAutoConfiguration
    @EnableRequestCorrelation
    @EnableFeignClients
    @RibbonClient(name = "local", configuration = LocalRibbonClientConfiguration.class)
    public static class Application {

        @Autowired
        private RestTemplate template;

        @Autowired
        private CorrelatedFeignClient feignClient;

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @RequestMapping(value = "/", method = RequestMethod.GET)
        public ResponseEntity<String> headerEcho(@RequestHeader(value = "X-Request-Id") String requestId) {

            return ResponseEntity.ok(requestId);
        }

        @RequestMapping(value = "/rest", method = RequestMethod.GET)
        public ResponseEntity propagateRestTemplate(@RequestHeader(value = "X-Request-Id") String requestId) {

            final String response = restTemplate().getForObject(url("/"), String.class);
            if(!requestId.equals(response)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok(response);
        }

        @RequestMapping(value = "/feign", method = RequestMethod.GET)
        public ResponseEntity propagateFeignClient(@RequestHeader(value = "X-Request-Id") String requestId) {

            final String response = feignClient.getRequestId();
            if(!requestId.equals(response)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok(response);
        }

        private String url(String path) {

            return ServletUriComponentsBuilder.fromCurrentRequest().replacePath(path).toUriString();
        }
    }

    @Configuration
    static class LocalRibbonClientConfiguration {

        @Value("${local.server.port}")
        private int port = 0;

        @Bean
        public ILoadBalancer ribbonLoadBalancer() {
            BaseLoadBalancer balancer = new BaseLoadBalancer();
            balancer.setServersList(Collections.singletonList(new Server("localhost", this.port)));
            return balancer;
        }
    }
}
