package com.websiteSureau.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.websiteSureau.model.MyUser;
import com.websiteSureau.model.SiteNews;

@Service
public class EmailServiceImpl2 extends EmailServiceImpl {
	
	public void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel)
	        throws MessagingException {
	                
	Context thymeleafContext = new Context();
	thymeleafContext.setVariables(templateModel);
	String htmlBody = thymeleafTemplateEngine.process("newsletterTemplate.html", thymeleafContext);
	    
	sendHtmlMessage(to, subject, htmlBody);	
	}
	
	public void sendContactFormEmail(SiteNews newsletter, Iterable<MyUser> listOfEmailUsers)
            throws MessagingException, UnsupportedEncodingException {
    	
        Map<String, Object> model = new HashMap<String, Object>();
        
        String[] listOfNewsletterParaph = newsletter.getTexte().split("\n");
        
        model.put("newsletterTitle", newsletter.getNewsTitle());
        model.put("newsletterParaph", listOfNewsletterParaph);
        
        for (MyUser user: listOfEmailUsers) {
	        model.put("nameUser", user.getName());
			sendMessageUsingThymeleafTemplate(user.getEmail(), "Les petits dessins d'Eric Sureau", model);
        }
	}

}
