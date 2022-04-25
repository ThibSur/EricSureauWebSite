package com.websiteSureau.service;

import java.util.Optional;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.model.MyUser;
import com.websiteSureau.repository.FileAWSS3Repository;
import com.websiteSureau.repository.UserRepository;

import lombok.Data;
import net.bytebuddy.utility.RandomString;

@Data
@Service
@Transactional
public class UserService {
	
    @Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailServiceImpl mailService;
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private static final Logger LOG = LoggerFactory.getLogger(FileAWSS3Repository.class);
	
    public Optional<MyUser> getUser(final int id) {
        return userRepository.findById(id);
    }
    
    public MyUser getUserByUserName(final String name) {
    	return userRepository.findByUserName(name);
    }
    
    public Optional<MyUser> getUserByEmail(final String email) {
    	return userRepository.findByEmail(email);
    }
    
    public Iterable<MyUser> getUsers() {
        return userRepository.findAll();
    }
    
    public Iterable<MyUser> getUsersBySubscriptionTrue() {
        return userRepository.findByNewsletterSubscription(true);
    }
    
    public Iterable<MyUser> getUsersByAuthority(String role) {
        return userRepository.findByAuthority(role);
    }
    
    public Optional<MyUser> getUserByDrawingID(int drawingID) {
        return userRepository.findByDrawingID(drawingID);
    }

    public String getUserRole(String name) {
    	return userRepository.findByUserName(name).getAuthority();	
    }
    
    public void deleteUser(final int id) {
    	userRepository.deleteById(id);
    }
    
  //saves a new user but not activates him.
    public void saveUser(MyUser user, String siteURL) throws MessagingException {
    	user.setUserName(user.getEmail());
	   	userRepository.save(user);
	   	LOG.info("NewUser saved");
	   	mailService.sendNewAccountUserEmail(user,siteURL);
	}
    
	//for updating user fields and/or activating a new user.
    public void updateUser(MyUser user, String siteURL) throws MessagingException {
    	
    	if (getUser(user.getId()).isPresent()) {
	    	MyUser currentUser = getUser(user.getId()).get();
			Drawing drawing = user.getDrawing();
			if(drawing != currentUser.getDrawing()) {
				currentUser.setDrawing(drawing);
			}		
			boolean newsletterSubscription = user.isNewsletterSubscription();
			if (newsletterSubscription != currentUser.isNewsletterSubscription()) {
				currentUser.setNewsletterSubscription(newsletterSubscription);
			}			
			if (currentUser.getEnabled()==1 && user.getEnabled()==0) {
				currentUser.setEnabled(user.getEnabled());
				currentUser.setVerificationCode(null);
			}		
			if (currentUser.getEnabled()==0 && user.getEnabled()==1) {			  	
				String randomCode = RandomString.make(64);		    	
				currentUser.setVerificationCode(randomCode);
				currentUser.setAuthority("USER");
				currentUser.setEnabled(user.getEnabled());			
				mailService.sendVerificationEmail(currentUser, siteURL);
				LOG.info("User is activated");
			}		
			userRepository.save(currentUser);
    	}
    }
    
    //updating newsletter subscription.
    public String updateMyUserNewsletter(MyUser user) {		
    	MyUser currentUser = getUserByUserName(user.getUserName());
    	String message = "Votre inscription à la newsletter a bien été modifiée";     		
		boolean newsletterSubscription = user.isNewsletterSubscription();	    
		if (newsletterSubscription != currentUser.isNewsletterSubscription()) {
			currentUser.setNewsletterSubscription(newsletterSubscription);
			userRepository.save(currentUser);
			return message;
		} else { return null; }	
    }
    
    //reset password when user exist.
    public boolean resetPassword(MyUser user, String siteURL) throws MessagingException {
    	boolean result = false;	
    	MyUser u = userRepository.findByUserName(user.getEmail());   	
    	if (u.getEnabled()==1) {
    		String randomCode = RandomString.make(64);
    		u.setVerificationCode(randomCode);
    		mailService.sendResetPasswordEmail(u, siteURL);
    		userRepository.save(u);
    		result=true;
    	}  	 	
    	return result;
    }
    
    //reset password when user is present with a minimum of 4 char.
    public String savePassword(MyUser user) {
		String statutMethodSave = "confirm";	
	   	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();   	
		Optional<MyUser> e = getUser(user.getId());
		if (e.isPresent()) {
			MyUser currentUser = e.get();
			if (user.getUserName().equals(currentUser.getUserName())) {
				if (user.getPassword().length() >= 4) {
					currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
					currentUser.setVerificationCode(null);
				} else statutMethodSave = "error2";
				userRepository.save(currentUser);
			} else {
				statutMethodSave = "error";
			}
			return statutMethodSave;
		} else { return null; }
	}
    
    //Automatic validation of a user after a delay
    @Async
	public void activateUser(MyUser user, String siteURL, int seconds) throws MessagingException {		
    	try {
    		System.out.println("Execute activateUser asynchronously started - " + Thread.currentThread().getName());
    		Thread.sleep(seconds * 1000);
    	} catch (InterruptedException ie) {
    		Thread.currentThread().interrupt();
    		System.out.println("Execute method activateUser interrupted - " + Thread.currentThread().getName());
    	}
    	Optional<MyUser> e = getUser(user.getId());
    	if (e.isPresent() && e.get().getEnabled() == 0) {
    		String randomCode = RandomString.make(64);
    		user.setVerificationCode(randomCode);
    		user.setAuthority("USER");
    		user.setEnabled(1);
    		mailService.sendVerificationEmail(user, siteURL);
    		userRepository.save(user);
    		System.out.println("User is activated");
    	}
    	Thread.currentThread().interrupt();
    	System.out.println("Execute method activateUser finished - " + Thread.currentThread().getName());			
	}	

    //Deleting verification code after a delay
	@Async
	public void deleteVerificationCode(MyUser user, int seconds) {
		MyUser us = userRepository.findByUserName(user.getEmail());
		System.out.println("Execute deleteVerificationCode asynchronously started - " + Thread.currentThread().getName());
		try {
			Thread.sleep(seconds * 1000);
				if (us.getVerificationCode()!=null) {
					us.setVerificationCode(null);
					userRepository.save(us);	
				}
			Thread.currentThread().interrupt();
			System.out.println("Execute method deleteVerificationCode finished - " + Thread.currentThread().getName());
		 } catch (InterruptedException ie) {
		    	Thread.currentThread().interrupt();
		 }
	}
}
