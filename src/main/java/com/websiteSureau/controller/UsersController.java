package com.websiteSureau.controller;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websiteSureau.model.MyUser;
import com.websiteSureau.service.UserAsynchServiceImpl;

@Controller
@EnableAsync
public class UsersController {
	
	@Autowired
	private UserAsynchServiceImpl userService;
	
	@GetMapping("/createUser")
	public String createUser(Model model) {
    	
		MyUser s = new MyUser();
		model.addAttribute("user", s);
		
		return "formNewUser";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute MyUser user, HttpServletRequest request, RedirectAttributes attributes) 
			throws UnsupportedEncodingException, MessagingException {
		
		boolean userSaved = true;
		
		Iterable<MyUser> listUsers = userService.getUsers();
    	for (MyUser u : listUsers) {
    		if (u.getEmail().equals(user.getEmail())) {
    			userSaved = false; 
    			break;
    		}
    	}
    	
    	if (userSaved == true) {
    		userService.saveUser(user, getSiteURL(request));
    		userService.activateUser(user, getSiteURL(request));
    	}
    	
	    attributes.addFlashAttribute("userSaved", userSaved);
	    return "redirect:/createUser";
	}
	
	@PostMapping("/updateUser")
	public ModelAndView updateUser(@ModelAttribute MyUser user, HttpServletRequest request) 
			 throws UnsupportedEncodingException, MessagingException {
		
		String s = userService.updateUser(user, getSiteURL(request)); 
		
		if (s == "confirm") return new ModelAndView("redirect:/admin/?" + s);
		else return new ModelAndView("redirect:/updateUser/" + user.getId() + "?" + s);
	}
	 
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}  
	  
	@GetMapping("/updateUser/{id}")
	public String updateUser(@PathVariable("id") final int id, Model model) {
    	
		Optional<MyUser> s = userService.getUser(id);
		model.addAttribute("user", s);
		
		return "formUpdateUser";		
	}
	
	@GetMapping("/deleteUserPage")
	public String deleteUserConfirmationPage() {
		return "formDeleteUser";		
	}                                               
	                                                                                                               
	@GetMapping("/deleteUser/{id}")
	public ModelAndView deleteUser(@PathVariable("id") final int id) {
		userService.deleteUser(id);
		return new ModelAndView("redirect:/admin");		
	}                                                                                                        

	@GetMapping("/createPassword/{id}")
	public String savePassword(@PathVariable("id") final int id, Model model, @Param("code") String code) {
    	
		Optional<MyUser> u = userService.getUser(id);
		
		if (u.isPresent() && u.get().getEnabled()==1 && u.get().getVerificationCode().equals(code)) {
			
			model.addAttribute("user", u);
			return "createPassword";
			
		} else return null;
	}
	
	@PostMapping("/savePassword")
	public ModelAndView savePassword(@ModelAttribute MyUser user) {
		String s = userService.savePassword(user);
		if (s.equals("confirm")) { return new ModelAndView("redirect:/"); }
		else { return new ModelAndView("redirect:/createPassword/" + user.getId() + "?code=" + user.getVerificationCode() + "&" + s); }
	}
	
	
	@GetMapping("/resetPassword")
	public String getResetPasswordPage(Model model) {
	
		MyUser u = new MyUser();
		model.addAttribute("user", u);
		
	    return "resetPassword";
		
	}
	
	@PostMapping("/resetPassword")
	public String resetPassword(@ModelAttribute MyUser user, HttpServletRequest request, RedirectAttributes attributes) throws UnsupportedEncodingException, MessagingException {
		
		Boolean r = userService.resetPassword(user, getSiteURL(request));
		userService.deleteVerificationCode(user, 3600);
		
	    attributes.addFlashAttribute("passwordReseted", r);
	    
	    return "redirect:/resetPassword";
		
	}
}
