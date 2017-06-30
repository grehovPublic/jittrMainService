package jittr.research;

import jittr.account.JitterDto;
import java.util.Collection;
import jittr.services.DomainObjValidationError;

public interface ResearchService {
    
    /**
     * Return {@link IJudgeResearch} with given id from repository.
     * 
     * @param id unique id of research to be found.
     * @return 
     * 
     * @throws EntityNotFoundException if no research found with given id.
     * @throws IllegalArgumentException if argument is {@code null}.
     * @throws HttpClientErrorException if any unexpected errors during fetching from
     *         remote repository occurred.
     */
    public IJudgeResearch findOne(final Long id);
    
    /**
     * Return default {@link IJudgeResearch} from repository.
     * 
     * @return 
     * 
     * @throws EntityNotFoundException if repository doesn't contain default research.
     * @throws HttpClientErrorException if any unexpected errors during fetching from
     *         remote repository occurred.
     */
    public IJudgeResearch findOneDefault();   

    /**
     * Return collection of all accessible (free and personal) {@link IJudgeResearch}
     * for Jitter with given username.
     * 
     * @param username Jitter's username. See {@link JitterDto#getUsername()}.
     * 
     * @return collection of all accessible researches. May be empty. Doesn't contain
     *         duplicates.
     *                
     * @throws IllegalArgumentException if argument is {@code null} or not valid. 
     * @throws JitterNotFoundException if current Jitter is not found in repository. 
     * @throws HttpClientErrorException if any unexpected errors during fetching from
     *         remote repository occurred.   
     */
    public Collection<IJudgeResearch> findAll(final String username);
    
    /**
     * Return default collection of free {@link IJudgeResearch} for any visitor.
     * 
     * @return collection of all free researches. May be empty. Doesn't contain
     *         duplicates.
     *         
     * @throws JitterNotFoundException if default Jitter is not found in repository. 
     * @throws HttpClientErrorException if any unexpected errors during fetching from
     *         remote repository occurred.   
     */
    public Collection<IJudgeResearch> findAllDefault();
    
    /**
     * Persist given research to the repository.
     * 
     * @param research the research to be persisted.
     * @return persisted research.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}
     * @throws DomainObjValidationError if entity's dto to store rep. invariant is broken.
     */
    public IJudgeResearch save(final IJudgeResearch research)  throws DomainObjValidationError;
}
