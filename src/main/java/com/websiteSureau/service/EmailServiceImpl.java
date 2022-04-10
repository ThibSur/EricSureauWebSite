package com.websiteSureau.service;


import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.websiteSureau.model.Message;
import com.websiteSureau.model.MyUser;

@Primary
@Service("EmailService")
public class EmailServiceImpl implements EmailService {
	
	@Autowired
    private JavaMailSender emailSender;
	
	@Autowired
	protected SpringTemplateEngine thymeleafTemplateEngine;
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	@Override
	public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
		
		LOG.info("START... Sending email");
		
	    MimeMessage message = emailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	    helper.setFrom("ericsureau.fr@gmail.com");
	    helper.setTo(to);
	    helper.setSubject(subject);
	    helper.setText(htmlBody, true);
	    
	    try {
	    emailSender.send(message);
	    LOG.info("END... Email sent success");
	    } catch (Exception e) {
	    LOG.info("Error : Email sent failure");
	    }
	}

	public void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel)
		        throws MessagingException {
		                
		Context thymeleafContext = new Context();
		thymeleafContext.setVariables(templateModel);
		String htmlBody = thymeleafTemplateEngine.process("emailTemplate.html", thymeleafContext);
		    
		sendHtmlMessage(to, subject, htmlBody);	
	}
	
	public void sendVerificationEmail(MyUser user, String siteURL)
            throws MessagingException {
		
		String verifyURL = siteURL + "/createPassword/" + user.getId() + "?code=" + user.getVerificationCode();
		
        Map<String, Object> model = new HashMap<>();
        model.put("nameUser", user.getName());
        model.put("text", "Enregistrez votre mot de passe pour activer votre compte :");
        model.put("link", verifyURL);
        model.put("text2", "Bonne découverte.");
    	
        sendMessageUsingThymeleafTemplate(user.getEmail(), "Activation de votre compte ericsureau.fr", model);

	}
	
	public void sendResetPasswordEmail(MyUser user, String siteURL)
            throws MessagingException {

		String verifyURL = siteURL + "/createPassword/" + user.getId() + "?code=" + user.getVerificationCode();
		
        Map<String, Object> model = new HashMap<>();
        model.put("nameUser", user.getName());
        model.put("text", "Cliquez sur ce lien pour réinitialiser votre mot de passe : ");
        model.put("link", verifyURL);
        model.put("text2", "Ce lien restera valide 1h.");
    	
        sendMessageUsingThymeleafTemplate(user.getEmail(), "Réinitialiser votre mot de passe - ericsureau.fr", model);
	}
	
	
	public void sendNewAccountUserEmail(MyUser user, String siteURL)
            throws MessagingException {
    	
        String verifyURL = siteURL + "/updateUser/" + user.getId();
        
        Map<String, Object> model = new HashMap<>();
        model.put("nameUser", "Eric");
        model.put("text", "Une demande d'ouverture de compte vient d'être effectuée, pour valider la demande :");
        model.put("link", verifyURL);

		sendMessageUsingThymeleafTemplate("ericsureau.fr@gmail.com", "Ouverture de compte", model);
	}
	
	public void sendContactFormEmail(Message message)
            throws MessagingException {
    	        
        Map<String, Object> model = new HashMap<>();
        model.put("nameUser", "Eric");
        model.put("text", "Message de " + message.getName() + " " + message.getLastName() + " (" + message.getEmail() + ") : ");
        model.put("text2", message.getMessage());

		sendMessageUsingThymeleafTemplate("ericsureau.fr@gmail.com", "Nouveau message d'un utilisateur du site", model);
	}
}