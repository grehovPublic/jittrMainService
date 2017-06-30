package jittr.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;

import jittr.Jittr;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JitterServiceIntegrationTest {
  
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    
    @Value("${jittr.repository-rest.url.jitters}")
    private String jittersUrl;
   
    @Autowired
    private JitterService jitterService;
    
    private List<JitterDto> jitters;
    
    public JitterServiceIntegrationTest() {            
    }

    @Before
    public void setup() throws Exception {
        jitters = jitterService.findAll();
    } 
    
    /*
     * Testing strategy for:
     * ResponseEntity<JitterDto> merge(final JitterDto jitter):
     *        
     * Partitions:
     *    jitter: = null, wrong, correct new, correct present;
     *    
     *    # returns ResponseEntity with according http status:
     *          BAD_REQUEST, CREATED.
     *          containing merged valid JitterDto.
     */ 

    @Test
    public void testMerge_Null_BadRequest() {    
        thrown.expect(HttpClientErrorException.class);
        jitterService.merge(null);
    } 
    
    @Test
    public void testMerge_Wrong_BadRequest() {     
        thrown.expect(HttpClientErrorException.class);
        JitterDto jitter = new JitterDto(null, null, null, null, null, null);
        jitterService.merge(jitter);
    } 
    
    @Test
    public void testMerge_CorrectNew_Created() { 
        
        JitterDto jitter = jitters.stream()
                            .filter(j -> j.getUsername().equals("jittr"))
                            .findAny()
                            .orElseThrow(() -> new IllegalStateException());
        
        JitterDto updated = new JitterDto(jitter);
        updated.setFullName("Updated Name");
        updated.setPassword("NewPassword");       
        JitterDto merged  = jitterService.merge(updated);
        
        assertThat(merged.getFullName()).isEqualTo(updated.getFullName());
        assertThat(merged.getPassword()).isEqualTo(jitter.getPassword());
    } 
    
    /*
     * Testing strategy for:
     * ResponseEntity<List<JitterDto>> findAll():
     *        
     * Partitions:   
     *    # returns ResponseEntity with according http status:
     *          FOUND.
     *          containing list with all jitters in repository. 
     *          list.size = 0; = 1, > 1
     */
    
    final static int JITTERS_AMOUNT = 6;

    @Test
    public void testFindAll_ListSizeAboveOne_Found() { 
            
        List<JitterDto> found = jitterService.findAll();
        Assert.isTrue(found.size() == JITTERS_AMOUNT, "");
        Assert.isTrue(found.stream().anyMatch(jitter -> jitter.getUsername().equals("jittr")), "");
    }    
}
