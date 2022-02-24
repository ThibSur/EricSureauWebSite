package com.websiteSureau.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.model.Message;
import com.websiteSureau.model.MyUser;
import com.websiteSureau.model.SiteNews;
import com.websiteSureau.service.CalendarService;
import com.websiteSureau.service.FilesService;
import com.websiteSureau.service.SiteNewsService;
import com.websiteSureau.service.UserAsynchServiceImpl;

@Controller
public class WebsiteController {
	
	@Autowired
	private UserAsynchServiceImpl userService;
		
	@Autowired
	private FilesService serviceDrawing;
	
	@Autowired
	private SiteNewsService newsService;
	
	@Autowired
	private CalendarService calendarService;
    
	@GetMapping("/")
	public String getHomePage(Model model) {
		
	   	String[] titles = serviceDrawing.getComicsTitles();
    	model.addAttribute("bdTitles", titles);
    	
    	String userRole = userService.getUserRole();
	    model.addAttribute("userRole", userRole);
	    
	    Iterable<Drawing> drawings = serviceDrawing.getDrawings();
	    
	    String nameDrawingOfTheMonth = null;
		Drawing comicsOfTheMonth = new Drawing();
		
		for (Drawing d : drawings) {
			if (d.getType().equals("Dessin-du-Mois")) nameDrawingOfTheMonth=d.getName();
			if (d.getType().equals("BD_Accueil")) comicsOfTheMonth=d;
		}
		
		model.addAttribute("drawingMonth", nameDrawingOfTheMonth);
		model.addAttribute("comicsMonth", comicsOfTheMonth);
	    	
    	List<SiteNews> siteNews = newsService.getNews();
    	
    	ArrayList<String> siteNewsDate = newsService.getNewsDate(siteNews);
	    String[] dateInLetters = calendarService.createArrayWithDateinLetters(siteNewsDate);
	    
	    Collections.reverse(siteNews);
	    
	    model.addAttribute("siteNews", siteNews);
	    model.addAttribute("dateInLetters", dateInLetters);
	    model.addAttribute("siteNewsDate", siteNewsDate);
    	
		return "index";
	}
	
	@GetMapping("/admin")
	public String getAdminPage(Model model) {
		String[] titles = serviceDrawing.getComicsTitles();
    	model.addAttribute("bdTitles", titles);
    	
		Iterable<MyUser> listUser = userService.getUsers();
		model.addAttribute("users", listUser);
		
		Iterable<Drawing> drawings = serviceDrawing.getDrawings();
		model.addAttribute("drawings", drawings);
		
    	SiteNews sn = new SiteNews();
		model.addAttribute("siteNews", sn);
		
		List<SiteNews> listSiteNews = newsService.getNews();
		model.addAttribute("listSiteNews", listSiteNews);
		
		return "adminPage";
	}
	
    @GetMapping("/login")
    public ModelAndView getLoginPage(Model model){
   	    
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	model.addAttribute("currentUser", currentUser);
    	
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        
        return modelAndView;
    }
	
	@GetMapping("/legal")
	public String getLegalPage() {	
		return "legalnotices";
	}
	
	@GetMapping("/drawings")
	public String showDrawingsByType(Model model, @RequestParam int type)  {
		
		String[] titles = serviceDrawing.getComicsTitles();
	    model.addAttribute("bdTitles", titles);
	    	
	    String[] typesOfDrawings = {"Actualités", "Environnement", "Sport", "Personnel", "Caricatures"};
	    String typeofDrawing = typesOfDrawings[type];
	    	
	    Iterable<Drawing> listDrawings = serviceDrawing.getDrawings();
	    ArrayList<Drawing> listofDrawingsOfTheType = new ArrayList<Drawing>();
	    	
	    for (Drawing d : listDrawings) {
	    	if (d.getType().equals(typesOfDrawings[type])) { 
	    		listofDrawingsOfTheType.add(d); 
	    		}
	    	}
	    
	    ArrayList<String> drawingsDate = serviceDrawing.getDrawingsDate(listofDrawingsOfTheType);
	    String[] dateInLetters = calendarService.createArrayWithDateinLetters(drawingsDate);
	    
	    model.addAttribute("drawingsDate", drawingsDate);
	    model.addAttribute("dateInLetters", dateInLetters);
	    model.addAttribute("drawings", listofDrawingsOfTheType);
	    model.addAttribute("type", typeofDrawing);
	    	
	    return "drawings";
	}
	    
	@GetMapping("/bd")
	public String showComicsByTitle(Model model, @RequestParam int n) {
	    	
		Iterable<Drawing> listDrawings = serviceDrawing.getDrawings();
		   	
	    String[] titles = serviceDrawing.getComicsTitles();
		String titleSelectedBD = titles[n];	
		   	
		ArrayList<String> listofDrawingsOfTheType = new ArrayList<String>();
	    	
	    for (Drawing d : listDrawings) {
	    	if (d.getType().equals("BD") && d.getTitle().equals(titles[n])) listofDrawingsOfTheType.add(d.getName());
	    }
	    
	    Collections.sort(listofDrawingsOfTheType);
 
	    model.addAttribute("drawings", listofDrawingsOfTheType);    	
		model.addAttribute("bdTitles", titles);
		model.addAttribute("title", titleSelectedBD);
			
		return "bd";
	}
		
	@GetMapping("/displayImage")
	public String displayImage(@RequestParam String name, Model model) {
			
		String[] titles = serviceDrawing.getComicsTitles();
	    model.addAttribute("bdTitles", titles);
			
		Iterable<Drawing> listDrawings = serviceDrawing.getDrawings();
		String title = null;
		String type = null;
			
		for (Drawing d:listDrawings) {
			if (d.getName().equals(name)) {
				title = d.getTitle(); 
				type = d.getType(); 
			}
		}
		
		model.addAttribute("drawingName", name);
		model.addAttribute("drawingTitle", title);
		model.addAttribute("drawingType", type);
		
		return "displayImage";
	}
		
	@GetMapping("/contact")
	public String getContactPage(Model model) {
		Message m = new Message();
		model.addAttribute("message", m);
		return "contactpage";
	}
		
	@GetMapping("/author")
	public String getAuthorPage(Model model) {
		String[] titles = serviceDrawing.getComicsTitles();
	    model.addAttribute("bdTitles", titles);
	    	
		return "author";
	}
}