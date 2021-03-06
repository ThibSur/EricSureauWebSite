package com.websiteSureau.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.websiteSureau.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.websiteSureau.service.CalendarService;
import com.websiteSureau.service.DrawingService;
import com.websiteSureau.service.SiteNewsService;
import com.websiteSureau.service.UserReportingConnectionService;
import com.websiteSureau.service.UserService;

@Controller
public class WebsiteController {
	
	@Autowired
	private UserService userService;
		
	@Autowired
	private DrawingService serviceDrawing;
	
	@Autowired
	private SiteNewsService newsService;
	
	@Autowired
	private CalendarService calendarService;

	@Autowired
	private UserReportingConnectionService userConnectionService;
	
	@Autowired
	Map<String, Object> menuAttributes;
    
	// Get the home page of the site with public drawing, comics of the Month and news.
	@GetMapping("/")
	public String getHomePage(Model model) {			
		
		//add menu attributes in fragments.html
		menuAttributes = getMapOfAttributesForMenu();
		model.mergeAttributes(menuAttributes);
		
		//add drawing of the month and first page of current comics (to display in home page)
	    ArrayList<Drawing> drawingOfTheMonth = serviceDrawing.getDrawingsByType(DrawingType.MONTH_DRAWING);
	    ArrayList<Drawing> comicsOfTheMonth = serviceDrawing.getDrawingsByType(DrawingType.HOME_COMICS);
		if (!drawingOfTheMonth.isEmpty()) model.addAttribute("drawingMonth", drawingOfTheMonth.get(drawingOfTheMonth.size()-1));
		if (!comicsOfTheMonth.isEmpty()) model.addAttribute("comicsMonth", comicsOfTheMonth.get(0));
	    
		//add siteNews in homePage with their dates (and the most recent appears in first)
    	List<SiteNews> siteNews = newsService.getNewsByPrivateNewsEqualFalse();
    	ArrayList<String> siteNewsDate = newsService.getNewsDate(siteNews);
	    String[] dateInLetters = calendarService.createArrayWithDateinLetters(siteNewsDate);    
	    Collections.reverse(siteNews);   
	    model.addAttribute("siteNews", siteNews);
	    model.addAttribute("dateInLetters", dateInLetters);
	    model.addAttribute("siteNewsDate", siteNewsDate);
    	
		return "index";
	}
	
	// Get the adminPage (only for user with admin role) for managing users, drawings and siteNews.
	@GetMapping("/admin")
	public String getAdminPage(Model model) {
    	
		//manage users : add list of all users
		Iterable<MyUser> listUser = userService.getUsers();
		model.addAttribute("users", listUser);
		
		//manage connections : add connections number month
		int numberOfConnectionsMonth = userConnectionService.getConnectionsNumberFromUsersForTheCurrentMonth();
		model.addAttribute("numberOfConnectionsMonth", numberOfConnectionsMonth);

		//manage drawings : add new drawing for saving and add a list of drawings for choose a drawing to delete
		Drawing dr = new Drawing();
		model.addAttribute("drawing", dr);
		Iterable<Drawing> drawings = serviceDrawing.getDrawings();
		model.addAttribute("drawings", drawings);
		model.addAttribute("drawingTypes", DrawingType.TYPES);

		//manage siteNews : add new siteNews for saving and add a list of siteNews for choose a siteNews to delete
    	SiteNews sn = new SiteNews();
		model.addAttribute("siteNews", sn);
		List<SiteNews> listSiteNews = newsService.getNews();
		model.addAttribute("listSiteNews", listSiteNews);
	
		return "adminPage";
	}

	@GetMapping("/admin/monthsConnections")
	public String getmonthsConnections(Model model) {
		model.addAttribute("connectionsMonth", userConnectionService.getUserConnections());
		return "adminMonthsConnections";
	}
	
	// Get the custom loginPage.
    @GetMapping("/login")
    public ModelAndView getLoginPage(Model model){
   	    
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	model.addAttribute("currentUser", currentUser);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        
        return modelAndView;
    }
    
	// Get the custom accountPage of the user.
    @GetMapping("/account")
    public String getAccountPage(Model model){	
   	    
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
		menuAttributes = getMapOfAttributesForMenu();
		model.mergeAttributes(menuAttributes);
	    
		//add drawings to display depending on the type of drawing (sorted by dates)
	    String[] typesOfDrawings = {DrawingType.NEWS, DrawingType.ENVIRONMENT, DrawingType.SPORT, DrawingType.PERSONAL, DrawingType.CARICATURE};
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
		menuAttributes = getMapOfAttributesForMenu();
		model.mergeAttributes(menuAttributes);
	    
		//add pages of the comics to display depending on the type of drawing (=BD) and the title of the drawing 
		Iterable<Drawing> listDrawingsOfTheType = serviceDrawing.getDrawingsByType(DrawingType.COMICS);
		String[] titles = serviceDrawing.getComicsTitles();
		String titleSelectedBD = titles[id];
		ArrayList<String> pagesOfTheComics = new ArrayList<>();
	    for (Drawing d : listDrawingsOfTheType) {
	    	if (d.getTitle().equals(titles[id])) pagesOfTheComics.add(d.getName());
	    	}	    
	    Collections.sort(pagesOfTheComics);    
	    model.addAttribute("drawings", pagesOfTheComics);    	
		model.addAttribute("bdTitles", titles);
		model.addAttribute("title", titleSelectedBD);
	    
		//add a list of the pages of the comics and the current page (to select the page to display) 
	   	if (page==pagesOfTheComics.size()) page--;
		if (page==-1) page=0;	
		int[] pages = new int[pagesOfTheComics.size()];
		for (int i=0; i<pagesOfTheComics.size(); i++) {
			pages[i]=i;
			}  
		model.addAttribute("currentPage", page);
		model.addAttribute("pages", pages);
		model.addAttribute("id", id);
			
		return "comics";
	}
	
	@GetMapping("/comicsP")
	public String showComicsofTheMonth(Model model, @RequestParam byte page) {
		
		//add menu attributes in fragments.html
		menuAttributes = getMapOfAttributesForMenu();
		model.mergeAttributes(menuAttributes);
	    
		//add pages of the comics to display depending on the type of drawing (=BD_Accueil)
		Iterable<Drawing> listDrawingsOfTheType = serviceDrawing.getDrawingsByType(DrawingType.HOME_COMICS);
		ArrayList<String> pagesOfTheComics = new ArrayList<>();
	    for (Drawing d : listDrawingsOfTheType) {
	    	pagesOfTheComics.add(d.getName());
	    }    
	    Collections.sort(pagesOfTheComics);
	    model.addAttribute("drawings", pagesOfTheComics);

		//add the current page of the comics (to select the page to display)
	   	if (page==pagesOfTheComics.size()) page--;
		if (page==-1 || page==0) page=0;		
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
		menuAttributes = getMapOfAttributesForMenu();
		model.mergeAttributes(menuAttributes);
	    	
		return "author";
	}
	
	//get the attributes for the menu
	private Map<String, Object> getMapOfAttributesForMenu() {	
		String[] titles = serviceDrawing.getComicsTitles();
		menuAttributes.put("bdTitles", titles);
    	final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	MyUser user = userService.getUserByUserName(currentUser);
    	String userRole = null;
    	String logDrawing = "/img/logos/loginWhite.png";
    	String loginUserName = null;
    	if (!currentUser.equals("anonymousUser")) {
    		userRole = userService.getUserRole(currentUser);
    		loginUserName = user.getName();
        	if (user.getDrawing() != null) {
        		logDrawing = "/getFile" + "?name=" + user.getDrawing().getName();
        		}
    		}
    	menuAttributes.put("userRole", userRole);
    	menuAttributes.put("loginUserName", loginUserName);
    	menuAttributes.put("drawingName", logDrawing);
    	
		return menuAttributes;
	}
}