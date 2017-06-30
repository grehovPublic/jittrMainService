package jittr.account;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class JitterConnectionSignUpService implements ConnectionSignUp {

    public String execute(Connection<?> connection) {
        return connection.getKey().getProviderUserId().toString();
    }
}