package com.websiteSureau.controller;

import java.util.List;
import java.util.Optional;

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
		attributes.addFlashAttribute("message2", "Nouvelle actualité sauvegardée.");
		return "redirect:/admin";
	}
	
	@PostMapping("/updateNews")
	public String uptadeNews(@ModelAttribute SiteNews news, RedirectAttributes attributes) {
		Optional<SiteNews> opt = newsService.getNewsById(news.getId());
		if (opt.isPresent()) {
			SiteNews newsToUpdate = opt.get();
			newsToUpdate.setNewsTitle(news.getNewsTitle());
			newsToUpdate.setTexte(news.getTexte());
			newsToUpdate.setPrivateNews(news.isPrivateNews());
			newsService.addNews(newsToUpdate);
			attributes.addFlashAttribute("message2", "L'actualité a bien été modifiée.");
		} else { attributes.addFlashAttribute("message1", "Erreur : un problème est survenu.");}
		return "redirect:/admin";
	}
	
	@PostMapping("/deleteNews")
	public String deleteNews(@ModelAttribute SiteNews news, RedirectAttributes attributes) {
		int id = news.getId();
		newsService.deleteNews(id);
		attributes.addFlashAttribute("message2", "L'actualité a bien été supprimée.");
		return "redirect:/admin";
	}
	
	@PostMapping("/previewNewsletter")
	public String previewNewsletterBeforeSending(@ModelAttribute SiteNews newsletter, Model model) {
		String[] listOfNewsletterParaph = newsletter.getTexte().split("\n");       
        model.addAttribute("newsletterTitle", newsletter.getNewsTitle());
        model.addAttribute("newsletterParaph", listOfNewsletterParaph);
		return "newsletterTemplate";
	}
	
	@PostMapping("/sendNewsletter")
	public ModelAndView sendNewsletter(@ModelAttribute SiteNews newsletter, RedirectAttributes attributes) 
			throws MessagingException {
		List<MyUser> listUsers = (List<MyUser>) userService.getUsersBySubscriptionTrue();
		mailService2.sendNewsletter(newsletter, listUsers, 86400);
		attributes.addFlashAttribute("message2", "La newsletter a bien été envoyée.");
		return new ModelAndView("redirect:/admin");
	}
	
	@PostMapping("/sendNewsletterTest")
	public ModelAndView sendNewsletterTestToAdminUsers(@ModelAttribute SiteNews newsletter, RedirectAttributes attributes) 
			throws MessagingException {
		List<MyUser> listUsersToTest = (List<MyUser>) userService.getUsersByAuthority("ADMIN");
		mailService2.sendNewsletter(newsletter, listUsersToTest, 86400);
		attributes.addFlashAttribute("message2", "Le test a bien été envoyé.");
		return new ModelAndView("redirect:/admin");
	}
	
	@PostMapping("/newsManage")
	public ModelAndView getNewsletterPage(RedirectAttributes attributes, @RequestParam("newsId") int id) {    
		//manage siteNews : add new siteNews for saving and add a list of siteNews for choose a siteNews to delete
		Optional<SiteNews> newsletter = newsService.getNewsById(id);
		if (newsletter.isPresent()) {
			attributes.addFlashAttribute("newsletter", newsletter.get());
		}
		return new ModelAndView("redirect:/admin#newsManage");
	}

}
