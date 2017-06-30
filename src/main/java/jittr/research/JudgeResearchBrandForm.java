package jittr.research;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import static jittr.research.IJudgeResearch.*;

/**
 * Form for research in BRANDNAME sate.
 * 
 * @author Grehov
 *
 */
public class JudgeResearchBrandForm {
   
    @NotNull
    @Size(min = VALIDATION_BRANDNAME_SIZE_MIN, max = VALIDATION_BRANDNAME_SIZE_MAX,
        message = VALIDATE_NOTE_BRANDNAME_SIZE)
    private String name;

    /**
     * Return subject of  {@link IJudgeResearch}.
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set subject of  {@link IJudgeResearch}.
     * 
     * @param name. See {@link IJudgeResearch#getName()}
     */
    public void setName(String name) {
        this.name = name;
    }
}
