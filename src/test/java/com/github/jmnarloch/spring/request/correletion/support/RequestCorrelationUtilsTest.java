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

import com.github.jmnarloch.spring.request.correletion.filter.DefaultRequestCorrelation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link RequestCorrelationUtils} class.
 *
 * @author Jakub Narloch
 */
public class RequestCorrelationUtilsTest {

    @Before
    public void setUp() throws Exception {

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @After
    public void tearDown() throws Exception {

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldNotRetrieveRequestId() {

        // given
        RequestContextHolder.resetRequestAttributes();

        // when
        final String correlationId = RequestCorrelationUtils.getCurrentCorrelationId();

        // then
        assertNull(correlationId);
    }

    @Test
    public void shouldRetrieveRequestId() {

        // given
        final String requestId = UUID.randomUUID().toString();
        RequestContextHolder.getRequestAttributes().setAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME,
                new DefaultRequestCorrelation(requestId), RequestAttributes.SCOPE_REQUEST);

        // when
        final String correlationId = RequestCorrelationUtils.getCurrentCorrelationId();

        // then
        assertEquals(requestId, correlationId);
    }
}