package com.websiteSureau.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.model.Message;
import com.websiteSureau.model.MyUser;
import com.websiteSureau.model.SiteNews;
import com.websiteSureau.service.CalendarService;
import com.websiteSureau.service.FilesService;
import com.websiteSureau.service.SiteNewsService;
import com.websiteSureau.service.UserService;

@Controller
public class WebsiteController {
	
	@Autowired
	private UserService userService;
		
	@Autowired
	private FilesService serviceDrawing;
	
	@Autowired
	private SiteNewsService newsService;
	
	@Autowired
	private CalendarService calendarService;
    	
	@GetMapping("/")
	public String getHomePage(Model model) {		
		// Get the home page of the site with public drawing, comics of the Month and news.
		
		//add menu attributes in fragments.html
		String[] titles = serviceDrawing.getComicsTitles();
    	model.addAttribute("bdTitles", titles);
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	if (currentUser != "anonymousUser") {
	    	String userRole = userService.getUserRole(currentUser);
	    	if (userRole.equals("ROLE_ADMIN")) { model.addAttribute("roleAdmin", "Admin");}
		    }
    	MyUser user = userService.getUserByUserName(currentUser);
    	String logDrawing = "/img/logos/loginWhite.png";
    	String loginUserName = null;
    	if (currentUser != "anonymousUser") {
    		loginUserName = user.getName();
        	if (user.getDrawing() != null) {
        		logDrawing = "/images" + "?name=" + user.getDrawing().getName();
        		}
    		}
    	model.addAttribute("loginUserName", loginUserName);
    	model.addAttribute("drawingName", logDrawing);
    	//end of menu attributes
	    	    
	    ArrayList<Drawing> drawingOfTheMonth = serviceDrawing.getDrawingsByType("Dessin-du-Mois");
	    ArrayList<Drawing> comicsOfTheMonth = serviceDrawing.getDrawingsByType("BD_Accueil");
		
		model.addAttribute("drawingMonth", drawingOfTheMonth.get(drawingOfTheMonth.size()-1));
		if (!comicsOfTheMonth.isEmpty()) model.addAttribute("comicsMonth", comicsOfTheMonth.get(0));
	    	
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
		// Get the adminpage (only for user with admin role) for managing users, drawings and siteNews.
		
		//add menu attributes in fragments.html
		String[] titles = serviceDrawing.getComicsTitles();
    	model.addAttribute("bdTitles", titles);
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
	    model.addAttribute("roleAdmin", "Admin");
    	MyUser user = userService.getUserByUserName(currentUser);
    	String logDrawing = "/img/logos/loginWhite.png";
    	String loginUserName = null;
    	loginUserName = user.getName();
        if (user.getDrawing() != null) {
        	logDrawing = "/images" + "?name=" + user.getDrawing().getName();
        	}
    	model.addAttribute("loginUserName", loginUserName);
    	model.addAttribute("drawingName", logDrawing);
    	//end of menu attributes
    	
		Iterable<MyUser> listUser = userService.getUsers();
		model.addAttribute("users", listUser);
		
		Drawing dr = new Drawing();
		model.addAttribute("drawing", dr);
		
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
		// Get the custom loginPage.
   	    
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	model.addAttribute("currentUser", currentUser);
    	
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        
        return modelAndView;
    }
    
    @GetMapping("/account")
    public String getAccountPage(Model model){	
		// Get the custom accountPage of the user.
   	    
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	MyUser user = userService.getUserByUserName(currentUser);
    	model.addAttribute("user", user);
    	if (user.getDrawing() != null) {
    	model.addAttribute("drawingName", user.getDrawing().getName());
    	}
    	
        return "accountPage";
    }
	
	@GetMapping("/legal")
	public String getLegalPage() {	
		return "legalnotices";
	}
	
	@GetMapping("/drawings")
	public String showDrawingsByType(Model model, @RequestParam byte type)  {
		
		//add menu attributes in fragments.html
		String[] titles = serviceDrawing.getComicsTitles();
    	model.addAttribute("bdTitles", titles);
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
     	String userRole = userService.getUserRole(currentUser);
	    if (userRole.equals("ROLE_ADMIN")) { model.addAttribute("roleAdmin", "Admin");}
    	MyUser user = userService.getUserByUserName(currentUser);
    	String logDrawing = "/img/logos/loginWhite.png";
    	String loginUserName = null;
    	loginUserName = user.getName();
        if (user.getDrawing() != null) {
        	logDrawing = "/images" + "?name=" + user.getDrawing().getName();
        	}
    	model.addAttribute("loginUserName", loginUserName);
    	model.addAttribute("drawingName", logDrawing);
    	//end of menu attributes
	    	
	    String[] typesOfDrawings = {"Actualités", "Environnement", "Sport", "Personnel", "Caricatures"};
	    String typeofDrawing = typesOfDrawings[type];
	      
	    ArrayList<Drawing> listofDrawingsOfTheType = serviceDrawing.getDrawingsByType(typeofDrawing);
	    ArrayList<String> drawingsDate = serviceDrawing.getDrawingsDate(listofDrawingsOfTheType);
	    String[] dateInLetters = calendarService.createArrayWithDateinLetters(drawingsDate);
	    
	    model.addAttribute("drawingsDate", drawingsDate);
	    model.addAttribute("dateInLetters", dateInLetters);
	    model.addAttribute("drawings", listofDrawingsOfTheType);
	    model.addAttribute("type", typeofDrawing);
	    	
	    return "drawings";
	}
	    
	@GetMapping("/comics/{id}")
	public String showComicsByTitle(@PathVariable("id") final byte id, Model model, @RequestParam byte page) {
	    
		//add menu attributes in fragments.html
		String[] titles = serviceDrawing.getComicsTitles();
    	model.addAttribute("bdTitles", titles);
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
	    String userRole = userService.getUserRole(currentUser);
	    if (userRole.equals("ROLE_ADMIN")) { model.addAttribute("roleAdmin", "Admin");} 
    	MyUser user = userService.getUserByUserName(currentUser);
    	String logDrawing = "/img/logos/loginWhite.png";
    	String loginUserName = null;
    	loginUserName = user.getName();
        if (user.getDrawing() != null) {
        	logDrawing = "/images" + "?name=" + user.getDrawing().getName();
        	}	
    	model.addAttribute("loginUserName", loginUserName);
    	model.addAttribute("drawingName", logDrawing);
    	//end of menu attributes
	    
		Iterable<Drawing> listDrawings = serviceDrawing.getDrawings();
		
		String titleSelectedBD = titles[id];	
		   	
		ArrayList<String> pagesOfTheComics = new ArrayList<String>();
	    	
	    for (Drawing d : listDrawings) {
	    	if (d.getType().equals("BD") && d.getTitle().equals(titles[id])) pagesOfTheComics.add(d.getName());
	    }
	    
	    Collections.sort(pagesOfTheComics);
	    
	   	if (page==pagesOfTheComics.size()) page--;
		if (page==-1) page=0;
		
		int[] pages = new int[pagesOfTheComics.size()];
		for (int i=0; i<pagesOfTheComics.size(); i++) {
			pages[i]=i;
		}
	    
	    model.addAttribute("drawings", pagesOfTheComics);    	
		model.addAttribute("bdTitles", titles);
		model.addAttribute("title", titleSelectedBD);
		model.addAttribute("id", id);
		model.addAttribute("currentPage", page);
		model.addAttribute("pages", pages);
			
		return "comics";
	}
	
	@GetMapping("/comicsP")
	public String showComicsofTheMonth(Model model, @RequestParam byte page) {
		
		//add menu attributes in fragments.html
		String[] titles = serviceDrawing.getComicsTitles();
	    model.addAttribute("bdTitles", titles);
	    String logDrawing = "/img/logos/loginWhite.png";
	    model.addAttribute("drawingName", logDrawing);
	    //end of menu attributes
	    	
		Iterable<Drawing> listDrawings = serviceDrawing.getDrawings();		   	
		ArrayList<String> pagesOfTheComics = new ArrayList<String>();
	    	
	    for (Drawing d : listDrawings) {
	    	if (d.getType().equals("BD_Accueil")) pagesOfTheComics.add(d.getName());
	    }
	    
	    Collections.sort(pagesOfTheComics);

	   	if (page==pagesOfTheComics.size()) page--;
		if (page==-1 || page==0) page=0;
		
	    model.addAttribute("drawings", pagesOfTheComics);
	    model.addAttribute("currentPage", page);   
			
		return "comicsP";
	}
	
	@GetMapping("/displayImage")
	public String displayDrawingWithHisName(@RequestParam String name, Model model) {
					
		Drawing drawing = serviceDrawing.getDrawingByName(name);
		model.addAttribute("drawing", drawing);
		
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
		
		//add menu attributes in fragments.html
		String[] titles = serviceDrawing.getComicsTitles();
    	model.addAttribute("bdTitles", titles);
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	if (currentUser != "anonymousUser") {
	    	String userRole = userService.getUserRole(currentUser);
	    	if (userRole.equals("ROLE_ADMIN")) { model.addAttribute("roleAdmin", "Admin");}
		    }
    	MyUser user = userService.getUserByUserName(currentUser);
    	String logDrawing = "/img/logos/loginWhite.png";
    	String loginUserName = null;
    	if (currentUser != "anonymousUser") {
    		loginUserName = user.getName();
        	if (user.getDrawing() != null) {
        		logDrawing = "/images" + "?name=" + user.getDrawing().getName();
        		}
    		}
    	model.addAttribute("loginUserName", loginUserName);
    	model.addAttribute("drawingName", logDrawing);
    	//end of menu attributes
	    	
		return "author";
	}

}