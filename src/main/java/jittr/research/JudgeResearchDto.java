package jittr.research;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import jittr.account.JitterDto;


/**
 * Abstract class for all implementations of {@link IJudgeResearch}.
 * 
 * @author Grehov
 *
 * @param <N> type of the next state of this research.
 */
public abstract class JudgeResearchDto implements Serializable , IJudgeResearch {
      
    private static final long serialVersionUID = 1L;
    
    private Long id;

    @NotNull
    protected State state;
    
    @NotNull
    @Size(min = VALIDATION_BRANDNAME_SIZE_MIN, max = VALIDATION_BRANDNAME_SIZE_MAX,
        message = VALIDATE_NOTE_BRANDNAME_SIZE)
    protected String name;
    
    @NotNull
    private JitterDto jitter;
    
    public JudgeResearchDto (final String name, final State state) {
        this.name = name;
        this.state = state;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
  
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public State getState() {
        return this.state;
    }
    
    @Override
    public void setState(final State state) {
        this.state = state;
    }
    
    @Override
    public String getStateAsString() {
        return state.toString();
    }
    
    @Override
    public JitterDto getJitter() {
        return jitter;
    }

    @Override
    public void setJitter(JitterDto jitter) {
        this.jitter = jitter;
    }
   
    @Override
    public abstract IJudgeResearch getNextState();

}
