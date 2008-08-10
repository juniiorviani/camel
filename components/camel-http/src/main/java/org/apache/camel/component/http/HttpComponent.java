/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.http;

import java.net.URI;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.HeaderFilterStrategyAware;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.util.IntrospectionSupport;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 * Defines the <a href="http://activemq.apache.org/camel/http.html">HTTP
 * Component</a>
 *
 * @version $Revision$
 */
public class HttpComponent extends DefaultComponent<HttpExchange> implements HeaderFilterStrategyAware {

    private HttpClientConfigurer httpClientConfigurer;
    private HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
    private HeaderFilterStrategy headerFilterStrategy;

    public HttpComponent() {
        this.setHeaderFilterStrategy(new HttpHeaderFilterStrategy());
    }
    
    /**
     * Connects the URL specified on the endpoint to the specified processor.
     */
    public void connect(HttpConsumer consumer) throws Exception {
    }

    /**
     * Disconnects the URL specified on the endpoint from the specified
     * processor.
     */
    public void disconnect(HttpConsumer consumer) throws Exception {
    }

    public HttpClientConfigurer getHttpClientConfigurer() {
        return httpClientConfigurer;
    }

    public void setHttpClientConfigurer(HttpClientConfigurer httpClientConfigurer) {
        this.httpClientConfigurer = httpClientConfigurer;
    }

    public HttpConnectionManager getHttpConnectionManager() {
        return httpConnectionManager;
    }

    public void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    @Override
    protected Endpoint<HttpExchange> createEndpoint(String uri, String remaining, Map parameters)
        throws Exception {
        HttpClientParams params = new HttpClientParams();
        IntrospectionSupport.setProperties(params, parameters, "httpClient.");

        // validate http uri that end-user did not duplicate the http part that can be a common error
        URI httpUri = new URI(uri);
        String part = httpUri.getSchemeSpecificPart();
        if (part != null) {
            part = part.toLowerCase();
            if (part.startsWith("//http://") || part.startsWith("//https://")) {
                throw new ResolveEndpointFailedException(uri,
                    "The uri part is not configured correctly. You have duplicated the http(s) protocol.");
            }
        }

        return new HttpEndpoint(uri, this, httpUri, params, httpConnectionManager, httpClientConfigurer);
    }

    @Override
    protected boolean useIntrospectionOnEndpoint() {
        return false;
    }

    public HeaderFilterStrategy getHeaderFilterStrategy() {
        return headerFilterStrategy;
    }

    public void setHeaderFilterStrategy(HeaderFilterStrategy strategy) {
        headerFilterStrategy = strategy;
        
    }
}
