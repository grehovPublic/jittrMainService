package jittr.research;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jittr.account.JitterDto;

/**
 * Represents the research of the subject (word or phrase). Moves from BRANDNAME
 * to READY state.
 * 
 * @author Grehov
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = JudgeResearchBrandDto.class, name = "jittr.dto.JudgeResearchBrandDto"),
    @JsonSubTypes.Type(value = JudgeResearchLearningDto.class, name = "jittr.dto.JudgeResearchLearningDto")
})
public interface IJudgeResearch {
    
    public static final int VALIDATION_BRANDNAME_SIZE_MIN = 4;
    public static final int VALIDATION_BRANDNAME_SIZE_MAX = 32;    
    public static final String VALIDATE_NOTE_BRANDNAME_SIZE = "{brandname.size}";
    
    public static enum State {BRANDNAME, LEARNING, TREE, PAYMENT, PROCESSING, READY};
   
    /**
     * Return this research' unique id.
     * 
     * @return id.
     */
    public Long getId();

    /**
     * Set this research' unique id.
     * @param name
     */
    public void setId(Long id);
    
    /**
     * Return current research' name. Sequence of any characters of 3 to 32 
     * chars length.
     * 
     * @return name.
     */
    public String getName();
  
    /**
     * Set current research' name. See {@link IJudgeResearch#getName()}.
     * @param name
     */
    public void setName(final String name);
    
    /**
     * Returns current research' {@link State}.
     * 
     * @return current {@link State}.
     */
    public State getState();
    
    /**
     * Updates current research' {@link State}.
     * 
     * @param state new {@link State} of current research.
     */
    public void setState(final State state);
    
    /**
     * Returns current research' {@link State}.
     * 
     * @return String representation of current {@link State}.
     */
    public String getStateAsString();
     
     /**
      * Returns implementation of research' next state.
      *  
      * @return
      */
      public IJudgeResearch getNextState();
      
      
      /**
       * Returns {@link JitterDto}, the author of this research.
       * 
       * @return
       */
      public JitterDto getJitter();
      
      /**
       * Set {@link JitterDto}, the author of this research.
       */
      public void setJitter(final JitterDto jitter);
}
