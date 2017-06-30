package jittr.research;

import static jittr.config.SharedConstants.VALUE_NOT_NULL;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jittr.account.JitterDto;
import jittr.services.AbstractService;
import jittr.services.DomainObjValidationError;

@Service
public class ResearchServiceImpl extends AbstractService<IJudgeResearch> implements ResearchService {
    
    @Value("${jittr.repository-rest.app-password}")
    private String appPassword;
    
    @Autowired
    ResearchServiceImpl(final RestTemplate researchRepository, final Environment env) {
        super(researchRepository, IJudgeResearch.class, 
                env.getProperty("jittr.repository-rest.url.research"), 
                env.getProperty("jittr.repository-rest.url.researches"));
    }


    @Override
    @PostFilter("filterObject.jitter.username == principal.name ||" +
                "filterObject.jitter.username == ${jittr.app-name}")
    public IJudgeResearch findOne(final Long id) {
        Assert.notNull(id, VALUE_NOT_NULL);
        final HttpEntity<String> request = new HttpEntity<>(getJsonHeaders());
        
        final ResponseEntity<IJudgeResearch> response = 
                repository.exchange(entityUrl + id, HttpMethod.GET, request, 
                        new ParameterizedTypeReference<IJudgeResearch>() {});
        AbstractService.validateResponse(response);
        return response.getBody();
    }
    
    @Override
    public IJudgeResearch findOneDefault() {
        final HttpEntity<String> request = new HttpEntity<>(getJsonHeaders());
        
        final ResponseEntity<IJudgeResearch> response = 
                repository.exchange(entityUrl, HttpMethod.GET, request, 
                        new ParameterizedTypeReference<IJudgeResearch>() {});
        AbstractService.validateResponse(response);
        return response.getBody();
    }

    @Override
    public List<IJudgeResearch> findAll(final String username) {
        this.validateJitterUsername(username);
        Assert.notNull(username, VALUE_NOT_NULL);

        final HttpEntity<List<JudgeResearchDto>> request = 
                new HttpEntity<>(getJsonHeaders());       
        final ResponseEntity<List<IJudgeResearch>> response =
                repository.exchange(entitiesUrl + username, HttpMethod.GET,
                        request, new ParameterizedTypeReference<List<IJudgeResearch>>() {});
        AbstractService.validateResponse(response);
        return response.getBody();
    }
    
    @Override
    public Collection<IJudgeResearch> findAllDefault() {
        final HttpEntity<List<JudgeResearchDto>> request = 
                new HttpEntity<>(getJsonHeaders());
        
        final ResponseEntity<List<IJudgeResearch>> response =
                repository.exchange(entitiesUrl, HttpMethod.GET,
                        request, new ParameterizedTypeReference<List<IJudgeResearch>>() {});
        AbstractService.validateResponse(response);
        return response.getBody();
    }
    
    /**
     * @throws IllegalArgumentException if argument is {@literal null}
     * @throws DomainObjValidationError if entity's dto to store rep. invariant is broken.
     */
    @Override
    public IJudgeResearch save(IJudgeResearch research) throws DomainObjValidationError {
        Assert.notNull(research, VALUE_NOT_NULL);
        validate(entityClass.toString(), research);
        
        HttpEntity<IJudgeResearch> request = new HttpEntity<>(research, getJsonHeaders());
        ResponseEntity<IJudgeResearch> response = repository.exchange(entitiesUrl, 
                HttpMethod.POST, request, new ParameterizedTypeReference<IJudgeResearch>() {});
        AbstractService.validateResponse(response);       
        return response.getBody(); 
    }
       
     /*
     * Returns prepared Json headers.
     */
    protected static HttpHeaders getJsonHeaders(){        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
