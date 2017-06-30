package jittr.research;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jittr.Jittr;
import jittr.services.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ResearchServiceImplUnitTest {
    
    final static JudgeResearchDto RESEARCH_APPLE = new JudgeResearchBrandDto("Apple"); 
    
    @Value("${jittr.repository-rest.url.jitters-all}")
    protected String researchUrl;
    
    @Value("${jittr.repository-rest.url.jitters}")
    protected String researchesUrl;
    
    @InjectMocks
    private ResearchServiceImpl researchService; 

    @Mock
    private  RestTemplate restTemplateMock;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private final Principal correctPrincipal;
    private final Principal wrongPrincipal;
    
    public ResearchServiceImplUnitTest() {
        correctPrincipal = new Principal() {
            @Override
            public String getName() {
                return "jittr";
            }
        };     
        
        wrongPrincipal = new Principal() {
            @Override
            public String getName() {
                return "jittrr";
            }
        }; 
    }

	
    /*
     * Testing strategy for:
     * public JudgeResearchDto<? extends JudgeResearch<?,?>> findOne(final Long id):
     *        
     * Partitions:
     *    id: =LONG_MIN, < 0, = 0, > 0, =LONG_MAX;
     *    
     *    # returns valid JudgeResearchDto implemented by one of the representation, according 
     *    to research state; or: throw ResearchNotFoundException if not found;
     */ 	  
	

	@Test
	public void shouldThrowNotFoundExceptionWhenResearchNotFound() {
		// arrange
		thrown.expect(EntityNotFoundException.class);
		thrown.expectMessage("Could not find");
     
        ResponseEntity<JudgeResearchDto> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);        
        when(restTemplateMock.exchange(Matchers.anyString(), 
                Matchers.eq(HttpMethod.GET), 
                Matchers.<HttpEntity<JudgeResearchDto>>any(),
                Matchers.<ParameterizedTypeReference<JudgeResearchDto>>any()))
              .thenReturn(response);
		// act
		researchService.findOne(Long.MIN_VALUE);
	}

	@Test
	public void shouldReturnValidResearch() {
		// arrange        
        ResponseEntity<JudgeResearchDto> response = 
                new ResponseEntity<>(RESEARCH_APPLE, HttpStatus.FOUND);        
        when(restTemplateMock.exchange(Matchers.anyString(), 
                Matchers.eq(HttpMethod.GET), 
                Matchers.<HttpEntity<JudgeResearchDto>>any(),
                Matchers.<ParameterizedTypeReference<JudgeResearchDto>>any()))
              .thenReturn(response);
		// act
        IJudgeResearch found =  researchService.findOne(0L);       
        
		// assert
		assertThat(found.getName()).isEqualTo(RESEARCH_APPLE.getName());
		assertThat(found.getState()).isEqualTo(RESEARCH_APPLE.getState());
	}
	
    /*
     * Testing strategy for:
     * public Collection<JudgeResearchDto> findAll():
     *        
     * Partitions:   
     *    # returns collection of valid JudgeResearchDtoes:
     *                  collection.size: = 0, > 0; 
     */ 
    
    @Test
    public void shouldReturnEmptyCollection() {
        // arrange     
        ResponseEntity<Collection<JudgeResearchDto>> response = 
                new ResponseEntity<>(new ArrayList<JudgeResearchDto>(), HttpStatus.FOUND);        
        when(restTemplateMock.exchange(Matchers.anyString(), 
                      Matchers.eq(HttpMethod.GET), 
                      Matchers.<HttpEntity<Collection<JudgeResearchDto>>>any(),
                      Matchers.<ParameterizedTypeReference<Collection<JudgeResearchDto>>>any()))
                    .thenReturn(response);
        // act
        Collection<IJudgeResearch> foundList =  researchService.findAll(correctPrincipal.getName());       
        
        // assert
        assertThat(foundList.size()).isZero();
    }
    
    @Test
    public void shouldReturnNotEmptyCollection() {
        // arrange              
        ResponseEntity<Collection<JudgeResearchDto>> response = 
                new ResponseEntity<>(Arrays.asList(RESEARCH_APPLE), HttpStatus.FOUND);        
        when(restTemplateMock.exchange(Matchers.anyString(), 
                Matchers.eq(HttpMethod.GET), 
                Matchers.<HttpEntity<Collection<JudgeResearchDto>>>any(),
                Matchers.<ParameterizedTypeReference<Collection<JudgeResearchDto>>>any()))
              .thenReturn(response);
        // act
        Collection<IJudgeResearch> foundCollection =  researchService.findAll(correctPrincipal.getName());       
        
        // assert
        assertThat(foundCollection.size()).isEqualTo(response.getBody().size());
        assertThat(foundCollection.iterator().next().getName())
            .isEqualTo(response.getBody().iterator().next().getName());
        assertThat(foundCollection.iterator().next().getState())
            .isEqualTo(response.getBody().iterator().next().getState());
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
