package com.websiteSureau.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.websiteSureau.service.MyUserDetailsService;

//SpringSecurity configurations

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService userDetailsService;
    
    @Autowired
    private DataSource dataSource;

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
			http
			.authorizeRequests()
			 	.antMatchers("/", "/saveUser", "/createPassword/**", "/savePassword", "/createUser/**", "/resetPassword", //HomePage and UserManagement
			 				"/contact", "/sendMessage", "/comicsP**",	"/imagesP", "/displayImage", //contact and public comics / drawings
			 				 "/css/*.css", "/js/*.js", "/img/logos/**", "/fragments", "/legal", "/author") //other pages and css, logos, fragments.... 
			 			.permitAll()
			 	.antMatchers("/admin", //adminPage
			 				"/updateUser/*", "/updateUser", "/deleteUser/*", //UsersManagement
			 				"/uploadFile", "/deleteFile", "/drawingUpdatePage", "/updateDrawing", "/deleteFilePage", //FilesManagement
			 				"/saveNews", "/deleteNews", "/updateNews", "/newsManage", //siteNewsManagement
			 				"/previewNewsletter", "/sendNewsletterTest", "/sendNewsletter" //siteNewsManagement
			 				)
			 			.hasAnyRole("ADMIN")
				.anyRequest().hasAnyRole("USER", "ADMIN")
				.and()    
			.rememberMe()
		        .tokenRepository(persistentTokenRepository())
		        .userDetailsService(this.userDetailsService)
		        .tokenValiditySeconds(86400*90)
			.and()
		.formLogin()
			.loginPage("/login")
			.defaultSuccessUrl("/")
	        .failureUrl("/login")
	        .permitAll()
	        .and()
	    .logout()
	        .permitAll()
	        .and()
	    .sessionManagement()
            .maximumSessions(2)
            .and();
	}
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
        return db;
   }
    
}