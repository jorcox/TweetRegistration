package es.unizar.tmdad.ucode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import es.unizar.tmdad.ucode.social.SimpleSocialUsersDetailService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            		.antMatchers("/*.html")
            		.authenticated()
            		.antMatchers("/css/**/*", "/js/**/*", "/img/**/*", "/image/**/*", "/fonts/**/*", "/webjars/**/*", "/favicon.ico",
            				"/users", "/userlogin", "/connect/google", "/connect/facebook", "/{link}")
            		.permitAll()
//            		.anyRequest()
            		.antMatchers("/")
                	.authenticated()
            .and()
            	.formLogin()
            		.loginPage("/login")
            			.usernameParameter("email")
            			.passwordParameter("password")
            				.permitAll()
            .and()
                .logout()
                	.permitAll()
            .and()
            	.apply(new SpringSocialConfigurer())
            .and()
                .csrf()
                	.disable();
	}
    
    @Bean
	public SocialUserDetailsService socialUsersDetailService() {
		return new SimpleSocialUsersDetailService(userDetailsService());
	}
}