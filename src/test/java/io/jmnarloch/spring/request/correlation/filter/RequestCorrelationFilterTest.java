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
package io.jmnarloch.spring.request.correlation.filter;

import io.jmnarloch.spring.request.correlation.api.CorrelationIdGenerator;
import io.jmnarloch.spring.request.correlation.api.RequestCorrelation;
import io.jmnarloch.spring.request.correlation.api.RequestCorrelationInterceptor;
import io.jmnarloch.spring.request.correlation.generator.UuidGenerator;
import io.jmnarloch.spring.request.correlation.support.RequestCorrelationConsts;
import io.jmnarloch.spring.request.correlation.support.RequestCorrelationProperties;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@link RequestCorrelationFilter} class.
 *
 * @author Jakub Narloch
 */
public class RequestCorrelationFilterTest {

    private RequestCorrelationFilter instance;

    private CorrelationIdGenerator generator = new UuidGenerator();

    private List<RequestCorrelationInterceptor> interceptors = new ArrayList<>();

    private RequestCorrelationProperties properties = new RequestCorrelationProperties();

    @Before
    public void setUp() throws Exception {

        instance = new RequestCorrelationFilter(generator, interceptors, properties);
    }

    @Test
    public void shouldInitiateCorrelationId() throws IOException, ServletException {

        // given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain chain = new MockFilterChain();

        // when
        instance.doFilter(request, response, chain);

        // then
        assertNotNull(request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME));
        assertNotNull(((HttpServletRequest)chain.getRequest()).getHeader(RequestCorrelationConsts.HEADER_NAME));
    }

    @Test
    public void shouldUseExistingCorrelationId() throws IOException, ServletException {

        // given
        final String requestId = UUID.randomUUID().toString();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain chain = new MockFilterChain();

        request.addHeader(RequestCorrelationConsts.HEADER_NAME, requestId);

        // when
        instance.doFilter(request, response, chain);

        // then
        final Object requestCorrelation = request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME);
        assertNotNull(requestCorrelation);
        assertEquals(requestId, ((RequestCorrelation) requestCorrelation).getRequestId());

        final String header = ((HttpServletRequest) chain.getRequest()).getHeader(RequestCorrelationConsts.HEADER_NAME);
        assertNotNull(header);
        assertEquals(requestId, header);
    }

    @Test
    public void shouldUseCustomHeader() throws IOException, ServletException {

        // given
        final String headerName = "X-TraceId";
        final String requestId = UUID.randomUUID().toString();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain chain = new MockFilterChain();

        request.addHeader(headerName, requestId);
        properties.setHeaderName(headerName);

        // when
        instance.doFilter(request, response, chain);

        // then
        final Object requestCorrelation = request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME);
        assertNotNull(requestCorrelation);
        assertEquals(requestId, ((RequestCorrelation) requestCorrelation).getRequestId());

        final String header = ((HttpServletRequest) chain.getRequest()).getHeader(headerName);
        assertNotNull(header);
        assertEquals(requestId, header);
    }

    @Test
    public void shouldInvokeInterceptor() throws IOException, ServletException {

        // given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain chain = new MockFilterChain();

        final RequestCorrelationInterceptor interceptor = mock(RequestCorrelationInterceptor.class);
        interceptors.add(interceptor);

        // when
        instance.doFilter(request, response, chain);

        // then
        final String requestId = ((HttpServletRequest) chain.getRequest()).getHeader(RequestCorrelationConsts.HEADER_NAME);
        final RequestCorrelation correlationId = (RequestCorrelation) request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME);
        assertNotNull(requestId);
        assertNotNull(correlationId);
        assertEquals(requestId, correlationId.getRequestId());

        verify(interceptor).afterCorrelationIdSet(requestId);
        verify(interceptor).cleanUp(requestId);
    }
}