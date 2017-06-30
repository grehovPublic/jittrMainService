package jittr.research;

import jittr.dto.JittleDto;

public class JudgeResearchLearningDto extends JudgeResearchDto {
    
    private static final int HALF_READY = 70;
    private static final int READY = HALF_READY * 2;
    
    /**
     * The state of training of this research.
     *
     */
    public static enum ReadyState { NOT_READY, HALF_READY, READY}
    
    public JudgeResearchLearningDto() {
        this(null);
    }
    
    public JudgeResearchLearningDto(String name) {
        super(name, State.LEARNING);
    }
    
    @Override
    public JudgeResearchLearningDto getNextState() {
        return null;
    }
    
    /**
     * Returns next Jittle by subject.
     * 
     * @return
     */
    public JittleDto getNextJittle() {
        return null;
    }    

    /**
     * Saves graded Jittle.
     * 
     * @param judgment 
     */
    public void saveJittle(final JittleDto graded) {
    }
    
    /**
     * Return total amount of Jittles, graded as positive (POSITIVE, VERY_POSITIVE)
     * for this research.
     * 
     * @return
     */
    public int getPositiveCount() {
        return 0;
    }
 
    /**
     * Return total amount of Jittles, graded as negative (NEGATIVE, VERY_NEGATIVE)
     * for this research.
     * 
     * @return
     */
    public int getNegativeCount() {
        return 0;
    }
    
    /**
     * Return ready state of training, based on graded Jittles amount.
     * 
     * @return {@link ReadyState}
     */
    public ReadyState getReadyState() {
        final int positive = this.getPositiveCount();
        final int negative =  + this.getNegativeCount();
        
        return (positive < HALF_READY || negative < HALF_READY) ? ReadyState.NOT_READY :
            (positive < READY || negative < READY) ? ReadyState.HALF_READY : ReadyState.READY;
    }
}
