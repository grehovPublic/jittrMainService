package jittr.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jittr.services.AbstractService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static jittr.config.SharedConstants.*;


/**
 * Representation of Jitter's account service.
 * 
 * @author Grehov
 *
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class JitterService extends AbstractService<JitterDto> implements UserDetailsService {
    
    private static final String DEFAULT_EMAIL = "default@gmail.com";
	
    @Value("${jittr.repository-rest.url.jitters-merge}")
    private String jitterMergeUrl;
    
    @Value("${jittr.repository-rest.url.jitters-all}")
    private static String jittersAllUrl;
    
    @Value("${jittr.repository-rest.url.jitters}")
    private static String jittersUrl;
    
    @Autowired
    JitterService(RestTemplate jitterRepositoryRest) {
        super(jitterRepositoryRest, JitterDto.class, jittersUrl, jittersAllUrl);
    }
    
    @Override
    public JitterDto findOne(Long id) {
        return super.findOne(id);
    }
    
    @Override
    public List<JitterDto> findAll() {
        return super.findAll(); 
    }

    /**
     * Persists the account to the remote RESTful repository. Returns new,
     * if not found.
     * 
     * @param jitter {@link JitterDto} of current user.
     * @return response with according 'http' status
     *         and errors.
     *         Contains merged account.
     * 
     * @throws IllegalArgumentException if argument is {@code null}
     * @throws HttpClientErrorException if any errors during merging with
     *         remote repository occurred.
     */
	public JitterDto merge(final JitterDto jitter) {
	    Assert.notNull(jitter, VALUE_NOT_NULL);
        HttpEntity<JitterDto> request = new HttpEntity<>(jitter, getJsonHeaders());
        ResponseEntity<JitterDto> response = repository.exchange(jitterMergeUrl, 
                HttpMethod.POST, request, JitterDto.class);
        AbstractService.validateResponse(response);       
        return response.getBody(); 
	}	

	@Override
	public UserDetails loadUserByUsername(final String username)
	        throws UsernameNotFoundException {	  
	    return null;
	}
	
	/**
	 * Provides signing in of given {@link JitterDto}.
	 * 
	 * @param jitter 
	 */
	public void signin(JitterDto jitter) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(jitter));
	}
	
	/**
	 * Creates new {@link JitterDto} from {@link TwitterProfile}.
	 * 
	 * @param profile
	 * @return created Jitter.
	 * 
	 * @throws IllegalArgumentException if argument is {@literal null}.
	 */
    public JitterDto createJitter(final TwitterProfile profile) {
        Assert.notNull(profile, VALUE_NOT_NULL);
        return new JitterDto(profile.getId(), Long.toString(profile.getId()), appPassword,
                profile.getName(), DEFAULT_EMAIL, JitterDto.Role.ROLE_JITTER);
    }   
    
    /**
     * Creates new {@link JitterDto} from {@link UserProfile}.
     * 
     * @param profile
     * @param id unique id of twitter's user.
     * @return created Jitter.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     */
    public JitterDto createJitter(final UserProfile profile, final String id) {
        Assert.notNull(profile, VALUE_NOT_NULL);
        Assert.notNull(id, VALUE_NOT_NULL);
        return new JitterDto(Long.valueOf(id), profile.getUsername(), appPassword,
                profile.getName(), DEFAULT_EMAIL, JitterDto.Role.ROLE_JITTER);
    } 
    
    /**
     * Return {@link JitterDto} from current {@link Authentication}.
     * 
     * @return   {@link JitterDto}.
     * 
     * @throws IllegalArgumentExcepion if no authentication information is available.
     */
    public JitterDto getJitterFromAuth() {
        Assert.notNull(SecurityContextHolder.getContext().getAuthentication(), VALUE_NOT_NULL);
        return (JitterDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }  
	
	private Authentication authenticate(JitterDto jitter) {
		return new UsernamePasswordAuthenticationToken(createUser(jitter), null, 
		        Collections.singleton(createAuthority(jitter)));
	}
	
	private User createUser(JitterDto jitter) {
		return new User(jitter.getUsername(), jitter.getPassword(), 
		        Collections.singleton(createAuthority(jitter)));
	}
	
	private User createUser(Authentication auth) {
	    return new User(auth.getName(), null, auth.getAuthorities());
	}

	private GrantedAuthority createAuthority(JitterDto jitter) {
		return new SimpleGrantedAuthority(jitter.getRole().toString());
	}
}
