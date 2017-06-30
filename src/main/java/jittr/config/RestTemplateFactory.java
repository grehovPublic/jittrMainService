package jittr.config;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.HttpHost;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {

    private RestTemplate restTemplate;    
    
    @Value("${jittr.repository-rest.url.auth-host}")
    private String authHost;
    
    @Value("${jittr.repository-rest.url.auth-port}")
    private Integer authPort;
    
    @Value("${jittr.repository-rest.url.auth-schema}")
    private String authSchema;
    
    @Value("${jittr.repository-rest.app-name}")
    private String appUsername;
    
    @Value("${jittr.repository-rest.app-password}")
    private String appPassword;
    
    public RestTemplate getObject() {
        return restTemplate;
    }
    public Class<RestTemplate> getObjectType() {
        return RestTemplate.class;
    }
    public boolean isSingleton() {
        return true;
    }
    
    public void afterPropertiesSet() {
        HttpHost host = new HttpHost(authHost, authPort, authSchema);
        final ClientHttpRequestFactory requestFactory = 
                new HttpComponentsClientHttpRequestFactoryBasicAuth(host);
        restTemplate = new RestTemplate(requestFactory);
        
        restTemplate.getInterceptors()
            .add(new BasicAuthorizationInterceptor(appUsername, appPassword));
    }
}