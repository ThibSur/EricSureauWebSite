package com.websiteSureau.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.transaction.Transactional;

import com.websiteSureau.model.MyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.UserConnection;
import com.websiteSureau.repository.UserConnectionRepository;

@Service
@Transactional
public class UserReportingConnectionService extends UserService {
	
	@Autowired
	private UserConnectionRepository connectionsRepository;
	
	private static final Logger LOG = LoggerFactory.getLogger(UserReportingConnectionService.class);
	
	//Listen the authentication success
	@EventListener
	public void authenticationSuccess(AuthenticationSuccessEvent event) {
		LOG.info("successfulAuthentication : " + event.getAuthentication().getName());
		saveUserConnexion(event.getAuthentication().getName());
	}
	
	//Listen the authentication fail
	@EventListener
	public void authenticationFail(AuthenticationFailureBadCredentialsEvent event) {
		LOG.info("authenticationFailed : badCredentials");
	}

	//Return the number of connections from the current month only
	public int getConnectionsNumberFromUsersForTheCurrentMonth() {
		Iterable<MyUser> userWithConnectionsDate = userRepository.findByUserConnectionDate();
		int numberOfConnections = 0;
		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM");
		s.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")));
		String currentMonthDate = s.format(System.currentTimeMillis());
			for (MyUser user : userWithConnectionsDate) {
				Date date = null;
				try {
					date = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(user.getUserConnectionDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String dateUserConnection = s.format(date);
				if (currentMonthDate.equals(dateUserConnection)) {
					numberOfConnections++;
				}
			}
		return numberOfConnections;
	}

	public Iterable<UserConnection> getUserConnections() {
		return connectionsRepository.findAllByOrderByMonth();
	}

	//save number of connections in db
	public void saveMonthConnexionNumber(int numberOfConnections) {
		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM");
	    s.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")));
	    String date = s.format(System.currentTimeMillis());
		
	    UserConnection connexion = new UserConnection();
		if (connectionsRepository.findByMonth(date).isPresent()) {
			connexion = connectionsRepository.findByMonth(date).get();
			connexion.setNumberOfConnections(numberOfConnections);
		} else {
			connexion.setNumberOfConnections(numberOfConnections);
			connexion.setMonth(date);
		}
		connectionsRepository.save(connexion);
	}
	
	//save number of connections of the current month every 1st day of the month at midnight
	@Scheduled(cron = "0 59 23 * * ?", zone = "Europe/Paris")
	public void deleteUserConnexions() {
		int numberOfConnections = getConnectionsNumberFromUsersForTheCurrentMonth();
		saveMonthConnexionNumber(numberOfConnections);
		LOG.info("connectionsUsers of the month saved");
	}

	//Save each new connection in user db
	public void saveUserConnexion(String userName) {
		MyUser userConnected = userRepository.findByUserName(userName);
		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		s.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")));
		String date = s.format(System.currentTimeMillis());
		userConnected.setUserConnectionDate(date);
		userRepository.save(userConnected);
	}

}
