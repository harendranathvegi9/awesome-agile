package org.awesomeagile.integrations.hackpad;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.apache.http.protocol.HTTP;
import org.awesomeagile.model.document.PadIdentity;


public class HackpadClientTest {
    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;
    private HackpadClient client;
    
    public HackpadClientTest() {
        // TODO Auto-generated constructor stub
        restTemplate = new RestTemplate();
        client = new HackpadClient(restTemplate, "http://test");
    }

    @Before
    public void setUp() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void fullUrlTest() {
        assertEquals("http://test/bar", client.fullUrl("/bar"));
    }

    @Test
    public void getHackpadTest() {
        mockServer.expect(requestTo("http://test/api/1.0/pad/12345/content/latest.html"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("<html>works!</html>", MediaType.TEXT_HTML));
        
        String res = client.getHackpad(new PadIdentity("12345"));
        
        assertEquals("<html>works!</html>", res);
    }
    
    @Test
    public void createHackpadTest() {
        mockServer.expect(requestTo("http://test/api/1.0/pad/create"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header(HTTP.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE))
        .andExpect(content().string("The Title"))
        .andRespond(withSuccess("{\"padId\":\"C0E68BD495E9\"}", MediaType.APPLICATION_JSON));
        
        PadIdentity id = client.createHackpad("The Title");
        assertEquals("C0E68BD495E9", id.getPadId());
    }
}
