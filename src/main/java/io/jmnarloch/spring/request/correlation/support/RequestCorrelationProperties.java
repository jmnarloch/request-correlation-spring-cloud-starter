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

/**
 * The request correlation properties.
 *
 * @author Jakub Narloch
 */
public class RequestCorrelationProperties {

    /**
     * Header name.
     */
    private String headerName = RequestCorrelationConsts.HEADER_NAME;

    /**
     * Creates new instance of {@link RequestCorrelationProperties} class.
     */
    public RequestCorrelationProperties() {
    }

    /**
     * Retrieves the header names.
     *
     * @return the header names
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * Sets the header names.
     *
     * @param headerName the header names
     */
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
