package com.websiteSureau.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.websiteSureau.model.MyUser;
import com.websiteSureau.model.SiteNews;

@Service
@Transactional
public class EmailServiceImpl2 extends EmailServiceImpl {

	private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl2.class);

	public void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel)
	        throws MessagingException {
	                
		Context thymeleafContext = new Context();
		thymeleafContext.setVariables(templateModel);
		String htmlBody = thymeleafTemplateEngine.process("newsletterTemplate.html", thymeleafContext);

		sendHtmlMessage(to, subject, htmlBody);
	}
	
	@Async
	public void sendNewsletter(SiteNews newsletter, List<MyUser> listOfEmailUsers, int seconds)
            throws MessagingException {
    	
		//Send a newsletter to a list of users. 
		//If the users are more than 60, pause (during 24h) the sending every 60.
        Map<String, Object> model = new HashMap<>();
        String[] listOfNewsletterParaph = newsletter.getTexte().split("\n");
        model.put("newsletterTitle", newsletter.getNewsTitle());
        model.put("newsletterParaph", listOfNewsletterParaph);      
        if (listOfEmailUsers.size()>=60) {
        	for(int i=0, y=1; i<listOfEmailUsers.size(); i++, y++) {
        		model.put("nameUser", listOfEmailUsers.get(i).getName());
        		sendMessageUsingThymeleafTemplate(listOfEmailUsers.get(i).getEmail(), "Les petits dessins d'Eric Sureau", model);
	        	if (y==60) {
					LOG.info("newsletter sending -> break for 24h every 60 mails (to avoid gmail spam blockage)");
	        		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	        		Callable<Integer> task = () -> 0;
	        		ScheduledFuture<Integer> future = scheduler.schedule(task, seconds, TimeUnit.SECONDS);
				    try {
						y=future.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				    scheduler.shutdown();
					LOG.info("newsletter sending -> resume of the sending of mails");
				}
	        }
        } else {
			for (MyUser user : listOfEmailUsers) {
				model.put("nameUser", user.getName());
				sendMessageUsingThymeleafTemplate(user.getEmail(), "Les petits dessins d'Eric Sureau", model);
			}
        }       
	}
}
