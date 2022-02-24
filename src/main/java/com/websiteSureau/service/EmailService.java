package com.websiteSureau.service;

import javax.mail.MessagingException;

public interface EmailService {    
    void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException ;
}