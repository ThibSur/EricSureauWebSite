package com.websiteSureau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.MyUser;
import com.websiteSureau.repository.UserRepository;

import javax.transaction.Transactional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        
    	final MyUser myuser = userRepository.findByUserName(userName);
        
        if (myuser == null) {
            throw new UsernameNotFoundException(userName);
        }
        
	
        UserDetails user = User.withUsername(myuser.getUserName())
	        								.password(myuser.getPassword())
        									.authorities(myuser.getAuthority())
        									.disabled(myuser.getEnabled()==0)
        									.build();
        
        return user;      
    }

}