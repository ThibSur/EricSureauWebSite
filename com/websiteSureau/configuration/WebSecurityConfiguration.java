package com.websiteSureau.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.websiteSureau.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

			http.authorizeRequests()
		 	.antMatchers("/", "/contact", "/saveUser", "/createPassword/**", "/savePassword", 
		 				"/createUser/**", "/news", "/sendMessage", "/css/*.css", "/js/*.js",
		 				"/imagesP", "/img/logos/**", "/fragments", "/legal")
		 				.permitAll()
		 	.antMatchers("/admin", "/updateUser/*", "/uploadFile", "/deleteFile",
		 				"/deleteUserPage", "/deleteUser/*", "/saveNews", "/deleteNews")
		 				.hasAnyAuthority("ROLE_ADMIN")
			.anyRequest().hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
			.and()
		.formLogin()
			.loginPage("/login")
	        .permitAll()
	        .and()
	    .logout()
	        .permitAll();
	}
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}