package com.websiteSureau.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.MyUser;
import com.websiteSureau.repository.UserRepository;

import lombok.Data;
import net.bytebuddy.utility.RandomString;

@Data
@Service
public class UserService {
	
    @Autowired
	protected UserRepository userRepository;

	@Autowired
	protected EmailServiceImpl mailService;
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
    public Optional<MyUser> getUser(final int id) {
        return userRepository.findById(id);
    }
    
    public Iterable<MyUser> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(final int id) {
    	userRepository.deleteById(id);
    }

    public void saveUser(MyUser user, String siteURL) throws UnsupportedEncodingException, MessagingException {
		
    	//saves a new user but not activates him.
    	
    	user.setUserName(user.getEmail());
	   	userRepository.save(user);
	   	mailService.sendNewAccountUserEmail(user,siteURL);
	}
    
    public String updateUser(MyUser user, String siteURL) throws UnsupportedEncodingException, MessagingException {
    	
    	//for updating an user and/or activating a new user.
    	
    	String statutMethodSave = "confirm";	
	   	
    	Optional<MyUser> e = getUser(user.getId());
    	
    	if(e.isPresent()) {
    		
    		MyUser currentUser= e.get();

				String lastName = user.getLastName();
				if(lastName != currentUser.getLastName()) {
					currentUser.setLastName(lastName);
				}
				
				String name = user.getName();
				if(name != currentUser.getName()) {
					currentUser.setName(name);
				}
				
				if (currentUser.getEnabled()==1 && user.getEnabled()==0) {
					currentUser.setEnabled(user.getEnabled());
			    	currentUser.setVerificationCode(null);
				}
				
				if (currentUser.getEnabled()==0 && user.getEnabled()==1) {
				  	
			    	String randomCode = RandomString.make(64);
			    	
			    	currentUser.setVerificationCode(randomCode);
					currentUser.setAuthority("ROLE_USER");
					currentUser.setEnabled(user.getEnabled());
					
					mailService.sendVerificationEmail(currentUser, siteURL);
				}
				
		   		userRepository.save(currentUser);
		   		
		   		return statutMethodSave;
	
		   		
		   	} else { 
			   	statutMethodSave = "error1";
			   	return statutMethodSave;
		   	}
    }

    public boolean resetPassword(MyUser user, String siteURL) throws UnsupportedEncodingException, MessagingException {
    	
    	boolean result = false;
    	
    	Iterable<MyUser> listUsers = getUsers();
    	
    	for (MyUser u : listUsers) {
    		if (u.getEmail().equals(user.getEmail()) && u.getEnabled()==1) {
    			String randomCode = RandomString.make(64);
    			u.setVerificationCode(randomCode);
    			mailService.sendResetPasswordEmail(u, siteURL);
    			userRepository.save(u);
    			result=true;
    			break;
    		}
    	}  	
    	return result;
    }
    
    public String savePassword(MyUser user) {
    	
		String statutMethodSave = "confirm";	
	   	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	   	
		Optional<MyUser> e = getUser(user.getId());
		
		MyUser currentUser = e.get();

		if(e.isPresent() && user.getUserName().equals(currentUser.getUserName())) { 
				
				if (user.getPassword().length() >= 4) {
				currentUser.setPassword(passwordEncoder.encode(user.getPassword())); 
				currentUser.setVerificationCode(null);
				}
				else statutMethodSave = "error2";
					
		   		userRepository.save(currentUser);
								   	
		   		return statutMethodSave;  
		   		
		   	} else { 
			   	statutMethodSave = "error";
			   	return statutMethodSave;
		   	}
	}
    
    public String getUserRole() {
    	
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
	
    	String role = null;
    	
		Iterable<MyUser> listUsers = getUsers();
		for (MyUser u : listUsers) {
			if (u.getUserName().equals(currentUser))
				role = u.getAuthority();
		}
		
		return role;
    }
    
    @Async
	public void activateUser(MyUser user, String siteURL) throws UnsupportedEncodingException, MessagingException {
		 
		 //Automatic validation of an user after a delay
		 
		 System.out.println("Execute activateUser asynchronously started - " + Thread.currentThread().getName());
	   
	    try {
	        Thread.sleep(86400 * 1000);	        
	        Optional<MyUser> e = getUser(user.getId());
	    		
	    	if (e.get().getEnabled()==0) {
				  	
			    String randomCode = RandomString.make(64);
			    	
			    user.setVerificationCode(randomCode);
				user.setAuthority("ROLE_USER");
				user.setEnabled(1);
				
				mailService.sendVerificationEmail(user, siteURL);
				userRepository.save(user);
				
				System.out.println("User is activated");
	    	} 
	        
			Thread.currentThread().interrupt();
			
			System.out.println("Execute method activateUser finished - " + Thread.currentThread().getName());
			
	    } catch (InterruptedException ie) {
	    	
	    	Thread.currentThread().interrupt();
	    }
	    
	 }

	@Async
	public void deleteVerificationCode(MyUser user, int time) throws UnsupportedEncodingException, MessagingException {
		
		 //Deleting verification code after a delay

		Iterable<MyUser> listUsers = getUsers();
		
		System.out.println("Execute deleteVerificationCode asynchronously started - " + Thread.currentThread().getName());
			 
		try {
			Thread.sleep(time * 1000);
				for (MyUser us : listUsers) {
					if (us.getEmail().equals(user.getEmail()) && us.getVerificationCode()!=null) {
						us.setVerificationCode(null);
						userRepository.save(us);	
						break;
			    	}
				}	
			Thread.currentThread().interrupt();
			
			System.out.println("Execute method deleteVerificationCode finished - " + Thread.currentThread().getName());
			
		 } catch (InterruptedException ie) {
		    	Thread.currentThread().interrupt();
		 }
	}
}
