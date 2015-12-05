package org.awesomeagile.integrations.hackpad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.protocol.HTTP;
import org.awesomeagile.model.document.PadIdentity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


public class HackpadClientTest {
    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;
    private RestTemplateHackpadClient client;
    
    public HackpadClientTest() {
        // TODO Auto-generated constructor stub
        restTemplate = new RestTemplate();
        client = new RestTemplateHackpadClient(restTemplate, "http://test");
    }

    @Before
    public void setUp() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testFullUrl() {
        assertEquals("http://test/bar", client.fullUrl("/bar"));
    }

    @Test
    public void testGetHackpad() {
        mockServer.expect(requestTo("http://test/api/1.0/pad/12345/content/latest.html"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("<html>works!</html>", MediaType.TEXT_HTML));
        
        String res = client.getHackpad(new PadIdentity("12345"));
        assertEquals("<html>works!</html>", res);
    }
    
    @Test
    public void testCreateHackpad() {
        mockServer.expect(requestTo("http://test/api/1.0/pad/create"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header(HTTP.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE))
        .andExpect(content().string("The Title"))
        .andRespond(withSuccess("{\"padId\":\"C0E68BD495E9\"}", MediaType.APPLICATION_JSON));
        
        PadIdentity id = client.createHackpad("The Title");
        assertEquals("C0E68BD495E9", id.getPadId());
    }

    @Test
    public void testUpdateHackpad() {
        String newContent = "<html><body>This is a new content</body></html>";
        String padId = RandomStringUtils.randomAlphanumeric(16);
        mockServer.expect(requestTo("http://test/api/1.0/pad/" + padId + "/content"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header(HTTP.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE))
            .andExpect(content().string(newContent))
            .andRespond(withSuccess("{\"success\": true}", MediaType.APPLICATION_JSON));

        client.updateHackpad(new PadIdentity(padId), newContent);
        mockServer.verify();
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateHackpadFailure() {
        String newContent = "<html><body>This is a new content</body></html>";
        String padId = RandomStringUtils.randomAlphanumeric(16);
        mockServer.expect(requestTo("http://test/api/1.0/pad/" + padId + "/content"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header(HTTP.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE))
            .andExpect(content().string(newContent))
            .andRespond(withSuccess("{\"success\": false}", MediaType.APPLICATION_JSON));
        client.updateHackpad(new PadIdentity(padId), newContent);
    }
}
