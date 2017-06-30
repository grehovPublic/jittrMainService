package jittr.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import jittr.account.JitterDto;
import jittr.account.JitterService;

@Service
public class TwitterSignInAdapter implements SignInAdapter {

    private JitterService jitterService;

    
    @Autowired
    public
    TwitterSignInAdapter (JitterService jitterService) {
        this.jitterService = jitterService;
    }

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {        
        org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
        String userId = connection.getKey().getProviderUserId().toString();
        JitterDto jitter = jitterService.merge(jitterService.createJitter(profile, userId));
        
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(jitter,
                    null, Arrays.asList(new SimpleGrantedAuthority(jitter.getRole().toString()))));
        return null;
    }
}