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
package io.jmnarloch.spring.request.correlation.filter;

import io.jmnarloch.spring.request.correlation.api.CorrelationIdGenerator;
import io.jmnarloch.spring.request.correlation.api.RequestCorrelation;
import io.jmnarloch.spring.request.correlation.api.RequestCorrelationInterceptor;
import io.jmnarloch.spring.request.correlation.support.RequestCorrelationConsts;
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

    /**
     * Logger instance used by this class.
     */
    private final Logger logger = LoggerFactory.getLogger(RequestCorrelationFilter.class);

    /**
     * The request generator used for generating new identifiers.
     */
    private final CorrelationIdGenerator correlationIdGenerator;

    /**
     * List of optional interceptors.
     */
    private final List<RequestCorrelationInterceptor> interceptors;

    /**
     * Creates new instance of {@link CorrelationIdGenerator} class.
     *
     * @param correlationIdGenerator the request id generator
     * @param interceptors       the correlation interceptors
     * @throws IllegalArgumentException if {@code requestIdGenerator} is {@code null}
     *                                  or {@code interceptors} is {@code null}
     */
    public RequestCorrelationFilter(CorrelationIdGenerator correlationIdGenerator, List<RequestCorrelationInterceptor> interceptors) {
        Assert.notNull(correlationIdGenerator, "Parameter 'correlationIdGenerator' can not be null.");
        Assert.notNull(interceptors, "Parameter 'interceptors' can not be null.");

        this.correlationIdGenerator = correlationIdGenerator;
        this.interceptors = interceptors;
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

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            doHttpFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            // otherwise just pass through
            chain.doFilter(request, response);
        }
    }

    /**
     * Performs 'enrichment' of incoming HTTP request.
     *
     * @param request  the http servlet request
     * @param response the http servlet response
     * @param chain    the filter processing chain
     * @throws IOException      if any error occurs
     * @throws ServletException if any error occurs
     */
    private void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // retrieves the correlationId
        String correlationId = getCorrelationId(request);

        // verifies the correlation id was set
        if (StringUtils.isBlank(correlationId)) {
            correlationId = generateCorrelationId();
            logger.debug("Request correlation id was not present, generating new one: {}", correlationId);
        }

        // triggers interceptors
        triggerInterceptors(correlationId);

        // instantiates new request correlation
        final RequestCorrelation requestCorrelation = new DefaultRequestCorrelation(correlationId);

        // populates the attribute
        final ServletRequest req = enrichRequest(request, requestCorrelation);

        // proceeds with execution
        chain.doFilter(req, response);
    }

    /**
     * Retrieves the correlation id from the request, if present.
     *
     * @param request the http servlet request
     * @return the correlation id
     */
    private String getCorrelationId(HttpServletRequest request) {

        return request.getHeader(RequestCorrelationConsts.HEADER_NAME);
    }

    /**
     * Generates new correlation id.
     *
     * @return the correlation id
     */
    private String generateCorrelationId() {

        return correlationIdGenerator.generate();
    }

    /**
     * Triggers the configured interceptors.
     *
     * @param correlationId the correlation id
     */
    private void triggerInterceptors(String correlationId) {

        for (RequestCorrelationInterceptor interceptor : interceptors) {
            interceptor.afterCorrelationIdSet(correlationId);
        }
    }

    /**
     * "Enriches" the request.
     *
     * @param request       the http servlet request
     * @param correlationId the correlation id
     * @return the servlet request
     */
    private ServletRequest enrichRequest(HttpServletRequest request, RequestCorrelation correlationId) {

        final CorrelatedServletRequest req = new CorrelatedServletRequest(request);
        req.setAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME, correlationId);
        req.setHeader(RequestCorrelationConsts.HEADER_NAME, correlationId.getRequestId());
        return req;
    }

    /**
     * An http servlet wrapper that allows to register additional HTTP headers.
     *
     * @author Jakub Narloch
     */
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

        /**
         * Sets the header value.
         *
         * @param key   the header name
         * @param value the header value
         */
        public void setHeader(String key, String value) {

            this.additionalHeaders.put(key, value);
        }

        @Override
        public String getHeader(String name) {
            if (additionalHeaders.containsKey(name)) {
                return additionalHeaders.get(name);
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {

            final List<String> values = new ArrayList<>();
            if (additionalHeaders.containsKey(name)) {
                values.add(additionalHeaders.get(name));
            } else {
                values.addAll(Collections.list(super.getHeaders(name)));
            }
            return Collections.enumeration(values);
        }

        @Override
        public Enumeration<String> getHeaderNames() {

            final Set<String> names = new HashSet<>();
            names.addAll(additionalHeaders.keySet());
            names.addAll(Collections.list(super.getHeaderNames()));
            return Collections.enumeration(names);
        }
    }
}
