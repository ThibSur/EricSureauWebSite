package com.websiteSureau.controller;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.websiteSureau.model.Message;
import com.websiteSureau.service.EmailServiceImpl;

@Controller
public class MailController {
	
	@Autowired
	private EmailServiceImpl mailService;

	@PostMapping("/sendMessage")
	public ModelAndView sendMessage(@ModelAttribute Message message) throws MessagingException {
		mailService.sendContactFormEmail(message);
		return new ModelAndView("redirect:/");
	}
	
}
