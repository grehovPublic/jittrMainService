package jittr.services;

/**
 * 
 * @author Grehov
 *
 */
public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String id) {
        super("Could not find domain object '" + id + "'.");
    }
}
