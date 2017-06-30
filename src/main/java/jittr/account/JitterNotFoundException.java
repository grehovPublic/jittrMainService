package jittr.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Grehov
 *
 */
public class JitterNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JitterNotFoundException(String userId) {
        super("Could not find user '" + userId + "'.");
    }
}
