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
package com.github.jmnarloch.spring.request.correletion.filter;

import com.github.jmnarloch.spring.request.correletion.api.RequestIdGenerator;
import com.github.jmnarloch.spring.request.correletion.api.RequestCorrelation;
import com.github.jmnarloch.spring.request.correletion.support.RequestCorrelationConsts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class RequestCorrelationFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(RequestCorrelationFilter.class);

    private final RequestIdGenerator requestIdGenerator;

    public RequestCorrelationFilter(RequestIdGenerator requestIdGenerator) {
        Assert.notNull(requestIdGenerator, "Parameter 'correlationIdGenerator' can not be null.");

        this.requestIdGenerator = requestIdGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // empty method
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // empty method
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            doHttpFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        }

        // otherwise just pass through
        chain.doFilter(request, response);
    }

    private void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // retrieves the correlationId
        String correlationId = getCorrelationId(request);

        // verifies the correlation id was set
        if (StringUtils.isBlank(correlationId)) {

            correlationId = generateCorrelationId();
        }

        // instantiates new request correlation
        final RequestCorrelation requestCorrelation = new DefaultRequestCorrelation(correlationId);

        // populates the attribute
        final ServletRequest req = enrichRequest(request, requestCorrelation);

        // proceeds with execution
        chain.doFilter(req, response);
    }

    private String getCorrelationId(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).getHeader(RequestCorrelationConsts.HEADER_NAME);
        }

        return null;
    }

    private String generateCorrelationId() {

        return requestIdGenerator.generate();
    }

    private ServletRequest enrichRequest(HttpServletRequest request, RequestCorrelation correlationId) {

        final CorrelatedServletRequest req = new CorrelatedServletRequest(request);
        req.setAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME, correlationId);
        req.setHeader(RequestCorrelationConsts.HEADER_NAME, correlationId.getRequestId());
        return req;
    }

    private static class CorrelatedServletRequest extends HttpServletRequestWrapper {

        /**
         * Map with additional customizable headers.
         */
        private final Map<String, String> additionalHeaders = new ConcurrentHashMap<>();

        /**
         * Creates a ServletRequest adaptor wrapping the given request object.
         *
         * @param request The request to wrap
         * @throws IllegalArgumentException if the request is null
         */
        public CorrelatedServletRequest(HttpServletRequest request) {
            super(request);
        }

        public void setHeader(String key, String value) {

            this.additionalHeaders.put(key, value);
        }

        @Override
        public String getHeader(String name) {
            if(additionalHeaders.containsKey(name)) {
                return additionalHeaders.get(name);
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {

            final List<String> values = new ArrayList<>();
            if(additionalHeaders.containsKey(name)) {
                values.add(additionalHeaders.get(name));
            }
            values.addAll(Collections.list(super.getHeaders(name)));
            return Collections.enumeration(values);
        }

        @Override
        public Enumeration<String> getHeaderNames() {

            final List<String> names = new ArrayList<>();
            names.addAll(additionalHeaders.keySet());
            names.addAll(Collections.list(super.getHeaderNames()));
            return Collections.enumeration(names);
        }
    }
}
