package org.awesomeagile.webapp.test;

import org.awesomeagile.AwesomeAgileApplication;
import org.awesomeagile.data.test.TestDatabase;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AwesomeAgileApplication.class)
@WebAppConfiguration
@TestPropertySource(properties = {
    "spring.social.google.clientId=client",
    "spring.social.google.secret=secret",
    "spring.social.google.scope=scope" })
public class BasicSecurityConfigTest {

    private static final String DATABASE_NAME = "awesomeagile";

    @ClassRule
    public static TestDatabase testDatabase = new TestDatabase(
        DATABASE_NAME
    );

    @Configuration
    protected static class TestConfiguration {
        @Bean
        public DataSource getDataSource() {
            return testDatabase.getDataSource(DATABASE_NAME);
        }
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    public void rootPageIsUnprotected() throws Exception {
        mvc.perform(get("/"))
            .andExpect(isOkOrNotFound());
    }

    @Test
    public void indexPageIsUnprotected() throws Exception {
        mvc.perform(get("/index.html"))
            .andExpect(isOkOrNotFound());
    }

    @Test
    public void cssIsUnprotected() throws Exception {
        mvc.perform(get("/css/some.css"))
            .andExpect(isOkOrNotFound());
    }

    @Test
    public void imagesAreUnprotected() throws Exception {
        mvc.perform(get("/images/some.png"))
            .andExpect(isOkOrNotFound());
    }

    @Test
    public void nodeModulesAreUnprotected() throws Exception {
        mvc.perform(get("/node_modules/bootstrap/css/some.css"))
            .andExpect(isOkOrNotFound());
    }

    @Test
    public void apiEndpointsAreProtected() throws Exception {
        mvc.perform(get("/api"))
            .andExpect(isUnauthorized());
        mvc.perform(get("/api/foo"))
            .andExpect(isUnauthorized());
    }

    @Test
    public void arbitraryEndpointsAreProtected() throws Exception {
        mvc.perform(get("/garbage"))
            .andExpect(isUnauthorized());
    }

    /**
     * Returns a ResultMatcher that asserts that the result status is either 200 (Ok) or 404 (Not Found).
     *
     * @return a ResultMatcher
     */
    private ResultMatcher isOkOrNotFound() {
        return new ResultMatcher() {
            @Override
            public void match(MvcResult result) throws Exception {
                HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
                assertTrue("Response status", status == HttpStatus.OK || status == HttpStatus.NOT_FOUND);
            }
        };
    }

    /**
     * Returns a ResultMatcher that asserts that the result status is either 401 (Unauthorized).
     *
     * @return a ResultMatcher
     */
    private ResultMatcher isUnauthorized() {
        return new ResultMatcher() {
            @Override
            public void match(MvcResult result) throws Exception {
                HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
                assertTrue("Response status", status == HttpStatus.UNAUTHORIZED);
            }
        };
    }
}