package com.websiteSureau.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.websiteSureau.model.Message;
import com.websiteSureau.service.EmailService;

@Controller
public class MailController {
	
	@Autowired
	private EmailService mailService;

	@PostMapping("/sendMessage")
	public ModelAndView sendMessage(@ModelAttribute Message message) {
		String email = message.getMessage();
		String object = "ericsureau.fr : " + message.getName() + " " + message.getLastName() + " (" + message.getEmail() + ")"; 
		mailService.sendSimpleMessage("ericsureau.fr@gmail.com", object, email);
		return new ModelAndView("redirect:/");
	}
	
}
