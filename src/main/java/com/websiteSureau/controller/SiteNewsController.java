package com.websiteSureau.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websiteSureau.model.MyUser;
import com.websiteSureau.model.SiteNews;
import com.websiteSureau.service.EmailServiceImpl2;
import com.websiteSureau.service.SiteNewsService;
import com.websiteSureau.service.UserService;

@Controller
public class SiteNewsController {
	
	@Autowired
	private SiteNewsService newsService;
	
	@Autowired
	private EmailServiceImpl2 mailService2;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/saveNews")
	public String saveNews(@ModelAttribute SiteNews news, RedirectAttributes attributes) {
		newsService.addNews(news);
		attributes.addFlashAttribute("message5", "Nouvelle actualité ajoutée");
		return "redirect:/admin";
	}
	
	@PostMapping("/deleteNews")
	public String deleteNews(@RequestParam("newsId") int id, RedirectAttributes attributes) {
		newsService.deleteNews(id);
		attributes.addFlashAttribute("message6", "L'actualité a bien été supprimée");
		return "redirect:/admin";
	}
	
	@PostMapping("/sendNewsletter")
	public ModelAndView sendNewsletter(@ModelAttribute SiteNews newsletter) throws UnsupportedEncodingException, MessagingException {
		Iterable<MyUser> listUser = userService.getUsersBySubscriptionTrue();
		mailService2.sendContactFormEmail(newsletter, listUser);
		return new ModelAndView("redirect:/");
	}

}
