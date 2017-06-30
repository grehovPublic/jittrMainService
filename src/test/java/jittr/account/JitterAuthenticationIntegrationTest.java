package jittr.account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jittr.Jittr;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JitterAuthenticationIntegrationTest {

    private static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Inject
    private FilterChainProxy springSecurityFilterChain;


    @Autowired
    private WebApplicationContext webApplicationContext;
    

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .addFilters(this.springSecurityFilterChain)
                .build();
    } 

    
    @Test
    public void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/jitter"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void userAuthenticationFails() throws Exception {
        final String username = "user";
        mockMvc.perform(post("/authenticate").param("username", username).param("password", "invalid"))
                .andExpect(redirectedUrl("/?error=1"))
                .andExpect(r -> Assert.assertNull(r.getRequest().getSession().getAttribute(SEC_CONTEXT_ATTR)));
    }
}
