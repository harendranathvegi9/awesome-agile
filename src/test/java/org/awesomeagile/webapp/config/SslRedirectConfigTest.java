package org.awesomeagile.webapp.config;

/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------------------------------
 */
import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.net.HttpHeaders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SslRedirectConfig.class)
@WebAppConfiguration
@IntegrationTest
public class SslRedirectConfigTest {
    private Client client;
    private WebTarget target;
    private Response r;

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
        client.property(ClientProperties.FOLLOW_REDIRECTS, false);
    }

    @Test
    public void testBasic() {
        target = client.target("http://localhost:8887");
        r = target.request().get();
        assertEquals(302, r.getStatus());
        assertEquals("https://localhost/",
                r.getHeaderString(HttpHeaders.LOCATION));
    }

    @Test
    public void testComplex() {
        target = client.target("http://localhost:8887/foo/bar?baz=qux&qux=fnord");
        r = target.request().get();
        assertEquals(302, r.getStatus());
        assertEquals("https://localhost/foo/bar?baz=qux&qux=fnord",
                r.getHeaderString(HttpHeaders.LOCATION));
    }
}
