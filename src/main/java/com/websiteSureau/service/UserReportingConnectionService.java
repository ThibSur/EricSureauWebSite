package com.websiteSureau.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.UserConnection;
import com.websiteSureau.repository.UserConnectionRepository;

@Service
@Transactional
public class UserReportingConnectionService {
	
	@Autowired
	UserConnectionRepository connectionsRepository;
	
	private static final Logger LOG = LoggerFactory.getLogger(UserReportingConnectionService.class);
	
	@EventListener
	public void authenticationSuccess(AuthenticationSuccessEvent event) {
		LOG.info("authenticationSuccess");
		saveConnexion(event.getAuthentication().getName());
	}
	
	public int getConnexionsNumber() {
		return connectionsRepository.findAll().size();
	}
	
	//Save each new connection in db
	public void saveConnexion(String userName) {
		Optional<UserConnection> existingConnexion = connectionsRepository.findByUserName(userName);
		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    s.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"))); 
	    String date = s.format(System.currentTimeMillis());
		if (existingConnexion.isEmpty()) {
			UserConnection connexion = new UserConnection();
			connexion.setUserName(userName);
			connexion.setConnectionDate(date);
			connectionsRepository.save(connexion);
		} else {
			UserConnection connexion = existingConnexion.get();
			connexion.setConnectionDate(date);
			connectionsRepository.save(connexion);
		}
	}
	
	//Delete all connections every Sunday at midnight
	@Scheduled(cron = "0 0 0 * * MON", zone = "Europe/Paris")
	public void deleteUserConnexions() {
		connectionsRepository.deleteAll();
		LOG.info("connectionsUsers deleted");
	}
}
