package jittr.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;

import jittr.account.JitterConnectionSignUpService;
import jittr.account.JitterService;
import jittr.config.TwitterSignInAdapter;

/**
 * Configuration of web level security.
 * 
 * @author Grehov
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends  WebSecurityConfigurerAdapter  {
    
    @Autowired
    private UserDetailsService jitterDetailsService;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    private JitterConnectionSignUpService jitterConnectionSignup;
    
    @Value("${jittr.repository-rest.app-password}")
    private String rememberKey;
    

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {      
        auth
        .eraseCredentials(true)
        .userDetailsService(jitterDetailsService);
//        .passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception { 
        
//        http        FIRST
//        .authorizeRequests()
//        .antMatchers("/", "/resources/**", "/login", "/connect/twitter", "/hello").permitAll()
//        .anyRequest().authenticated()
//    .and()
//        .formLogin()
//        .loginPage("/?login")
//        .failureUrl("/?error=1")
//        .loginProcessingUrl("/authenticate")
//        .defaultSuccessUrl("/jitter")
//        .permitAll()
//    .and()
//        .logout()
//        .logoutUrl("/?logout")
//        .logoutSuccessUrl("/?logout")
//        .permitAll()
//        .deleteCookies("JSESSIONID")
//    .and()
//        .rememberMe()
//        .rememberMeServices(rememberMeServices())
//        .key(rememberKey)
//    .and()
//        .apply(new SpringSocialConfigurer()
//            .postLoginUrl("/?postlogin")
//            .alwaysUsePostLoginUrl(true));
        
        
        http
        .authorizeRequests()
            .antMatchers("/jitter/**").authenticated()
            .antMatchers("/research/new").authenticated()
        .and()
            .formLogin()
                .loginPage("/login")
                .failureUrl("/login?param.error=bad_credentials")
                .permitAll()
        .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
        .and()
            .rememberMe()
            .rememberMeServices(rememberMeServices())
            .key(rememberKey);
    }  
    
    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        return new TokenBasedRememberMeServices(rememberKey, jitterDetailsService);
    }
    
    @Bean
    public ProviderSignInController providerSignInController() {
        ((InMemoryUsersConnectionRepository) usersConnectionRepository)
            .setConnectionSignUp(jitterConnectionSignup);
        return new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository,
                new TwitterSignInAdapter((JitterService)jitterDetailsService));
    }
}
