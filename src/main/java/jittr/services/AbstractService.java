package jittr.services;

import static jittr.account.JitterDto.*;
import static jittr.config.SharedConstants.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * Abstract class for all implementations of domain types' services.
 * 
 * @author Grehov
 *
 * @param <T>
 *              the type for domain entity to handle.             
 */
public abstract class AbstractService<T> {
    
    public static final String DEFAULT_USERNAME = "jittr";
    public static final Long DEFAULT_ID = 1L;
       
    @Autowired
    protected Validator validator;
    
    @Value("${jittr.repository-rest.app-password}")
    protected String appPassword;
    
    protected final RestTemplate repository;
    
    protected final Class<T> entityClass;
    
    protected final String entityUrl;
    
    protected final String entitiesUrl;
    
    /**
     * Repository injection constructor.
     * 
     * @param repository {@link RestTemplate} client for remote repository of entities of
     * type {@link T}.
     * @param entityClass class object of {@link T} entity.
     * @param entityUrl path to RESTful endpoint of entity.
     * @param entityUrl path to RESTful endpoint of entities.
     */
    protected AbstractService(final RestTemplate repository, final Class<T> entityClass, 
            final String entityUrl, final String entitiesUrl) {
        this.repository = repository;
        this.entityClass = entityClass;
        this.entityUrl = entityUrl;
        this.entitiesUrl = entitiesUrl;
    }
    
    /**
     * Finds the domain entity of type {@link T} with given id from the repository. 
     * 
     * @param id unique identifier of the entity to be returned.
     * @return {@link T} object.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws EntityNotFoundException if entity with given id not found.
     */
    public T findOne(final Long id) {
        Assert.notNull(id, VALUE_NOT_NULL);
        
        HttpEntity<Long> request = new HttpEntity<>(id, getJsonHeaders());
        ResponseEntity<T> response = repository.exchange(entityUrl, 
                HttpMethod.POST, request, entityClass);
        AbstractService.validateResponse(response);       
        return response.getBody(); 
    }
    
    /**
     * Finds all {@link T} entities from repository.
     * 
     * @return list with found unique {@link TD} objects. May be empty.
     *          
     */
    public List<T> findAll() {      
        HttpEntity<Long> request = new HttpEntity<>(getJsonHeaders());
        ResponseEntity<List<T>> response = repository.exchange(entitiesUrl, HttpMethod.POST,
                request, new ParameterizedTypeReference<List<T>>() {});
        AbstractService.validateResponse(response);       
        return response.getBody(); 
    }
    
    /**
     * Persists the entity to the remote RESTful repository. Returns new,
     * if not found.
     * 
     * @param entity {@link T} type of entity to persist.
     * @return response persisted entity.
     * 
     * @throws IllegalArgumentException if argument is {@code null}
     * @throws DomainObjValidationError if entity's dto to store rep. invariant is broken.
     * @throws HttpClientErrorException if any errors during persisting with
     *         remote repository occurred.
     */
    public T save(final T entity) throws DomainObjValidationError {
        Assert.notNull(entity, VALUE_NOT_NULL);
        validate(entityClass.toString(), entity);
        
        HttpEntity<T> request = new HttpEntity<>(entity, getJsonHeaders());
        ResponseEntity<T> response = repository.exchange(entitiesUrl, 
                HttpMethod.POST, request, new ParameterizedTypeReference<T>() {});
        AbstractService.validateResponse(response);       
        return response.getBody(); 
    }
    
    /*
     * Checks domain objects for their representation invariant continuity.
     * 
     * @param objectName {@link T} name.
     * @param validated validated domain object of type {@link T}.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws DomainObjValidationError if rep. invariant of the validated object is broken.
     */
    protected void validate(final String objectName, final T validated) 
            throws DomainObjValidationError {
        Assert.notNull(objectName, VALUE_NOT_NULL);
        Assert.notNull(validated, VALUE_NOT_NULL);   
        
        BeanPropertyBindingResult bindingResult = 
                new BeanPropertyBindingResult(validated, objectName);
        validator.validate(validated, bindingResult);
 
        if (bindingResult.hasErrors()) {
            throw new DomainObjValidationError(bindingResult.getFieldErrors());
        }
    }
    
    /*
     * Checks the collection of domain objects for their representation invariant continuity.
     * 
     * @param validatedList validated collection of domain objects of type {@link T}.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws DomainObjValidationError if rep. invariant of any validated object is broken.
     */
    protected void validate(final Collection<T> validatedList) 
            throws DomainObjValidationError {
        for (T validated : validatedList) { 
            Assert.notNull(validated, VALUE_NOT_NULL);
            validate(validated.getClass().getName(), validated);
        }
    }

    
    /*
     * Produce HTTP headers for response.
     */
    protected HttpHeaders getHttpHeaders(final T dto, 
            final UriComponentsBuilder ucb, final String url) {
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ucb.path(url)
            .path(String.valueOf(dto))
            .build()
            .toUri();
        headers.setLocation(locationUri);   
        return headers;
    }

    /*
     * Validates response.
     */
    public static void validateResponse(ResponseEntity<?> response) {
        HttpStatus status = response.getStatusCode();
        
        if (response.getBody() == null)
            throw new IllegalStateException();
        
        switch(status) {
        case NOT_FOUND:
            throw new EntityNotFoundException(" details: " + response.getBody());
        case BAD_REQUEST:
            throw new IllegalArgumentException(" details: " + response.getBody());
        default:
            break;    
        }
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
   
   /*
    * Validates Jitter's username
    * .
    * @param username Jitter's unique username.
    * 
    * @throws IllegalArgumentException if validation errors occurs.
    */
   protected void validateJitterUsername(final String username) {
       Assert.notNull(username, VALUE_NOT_NULL);
       Assert.isTrue((username.length() >= VALIDATION_USERNAME_SIZE_MIN) &&
               (username.length() <= VALIDATION_USERNAME_SIZE_MAX), VALIDATE_NOTE_USERNAME_SIZE);
   }
}

