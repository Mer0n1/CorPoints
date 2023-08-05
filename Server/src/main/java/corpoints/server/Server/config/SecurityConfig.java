package corpoints.server.Server.config;


import corpoints.server.Server.sevices.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfiguration {

    @Autowired
    private AccountDetailsService service;


    @Autowired
    void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeHttpRequests( (auth) -> auth
                .requestMatchers("/groups/test","/accounts/register", "/accounts/authentication")
                .permitAll()
                .anyRequest().authenticated()
        )
                .formLogin()
                .loginProcessingUrl("/accounts/authentication")
                .defaultSuccessUrl("/accounts/AuthTrue", true) //при успешной аутентефикации
                .failureUrl("/accounts/AuthFalse"); //при неуспешной;

        return http.build();
    }

}

