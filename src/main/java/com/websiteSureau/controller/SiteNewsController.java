package com.websiteSureau.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		attributes.addFlashAttribute("message2", "Nouvelle actualité ajoutée en page d'accueil.");
		return "redirect:/admin";
	}
	
	@PostMapping("/deleteNews")
	public String deleteNews(@RequestParam("newsId") int id, RedirectAttributes attributes) {
		if (id==0) {
			attributes.addFlashAttribute("message1", "Veuillez sélectionner une actualité à supprimer.");
		} else {
			newsService.deleteNews(id);
			attributes.addFlashAttribute("message2", "L'actualité a bien été supprimée.");
		}
		return "redirect:/admin";
	}
	
	@PostMapping("/previewNewsletter")
	public String previewNewsletterBeforeSending(@ModelAttribute SiteNews newsletter, Model model) {
		String[] listOfNewsletterParaph = newsletter.getTexte().split("\n");       
        model.addAttribute("newsletterTitle", newsletter.getNewsTitle());
        model.addAttribute("newsletterParaph", listOfNewsletterParaph);
		return "newsletterPreview";
	}
	
	@PostMapping("/sendNewsletter")
	public ModelAndView sendNewsletter(@ModelAttribute SiteNews newsletter, RedirectAttributes attributes) 
			throws UnsupportedEncodingException, MessagingException {
		Iterable<MyUser> listUser = userService.getUsersBySubscriptionTrue();
		mailService2.sendContactFormEmail(newsletter, listUser);
		attributes.addFlashAttribute("message2", "L'actualité a bien été envoyée.");
		return new ModelAndView("redirect:/admin");
	}

}
