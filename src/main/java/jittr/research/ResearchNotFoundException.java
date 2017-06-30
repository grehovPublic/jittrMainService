package jittr.research;

/**
 * 
 * @author Grehov
 *
 */
public class ResearchNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResearchNotFoundException(String researchId) {
        super("Could not find research '" + researchId + "'.");
    }
}
