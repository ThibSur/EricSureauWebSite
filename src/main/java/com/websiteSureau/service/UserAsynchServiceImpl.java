package com.websiteSureau.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.MyUser;

import net.bytebuddy.utility.RandomString;

@Service
public class UserAsynchServiceImpl extends UserService {
	
	//this class implements userSerice by adding an automatic validation of an user after a delay

	 @Async
	 public void activateUser(MyUser user, String siteURL) throws UnsupportedEncodingException, MessagingException {
		 
		 System.out.println("Execute activateUser asynchronously started - " + Thread.currentThread().getName());
	   
	    try {
	    	
	        Thread.sleep(86400 * 1000);	        
	        Optional<MyUser> e = getUser(user.getId());
	    		
	    	if (e.get().getEnabled()==0) {
				  	
			    String randomCode = RandomString.make(64);
			    	
			    user.setVerificationCode(randomCode);
				user.setAuthority("ROLE_USER");
				user.setEnabled(1);
					
			    sendVerificationEmail(user, siteURL);
				userRepository.save(user);
				
				System.out.println("User is activated");
	    	} 
	        
			Thread.currentThread().interrupt();
			
			System.out.println("Execute method activateUser finished - " + Thread.currentThread().getName());
			
	    } catch (InterruptedException ie) {
	    	
	    	Thread.currentThread().interrupt();
	    }
	    
	 }

}
