package com.websiteSureau.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.websiteSureau.service.UserExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.model.MyUser;
import com.websiteSureau.service.DrawingService;
import com.websiteSureau.service.UserService;

@Controller
@EnableAsync
public class UsersController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DrawingService serviceDrawing;
		 
	@GetMapping("/createUser")
	public String createUser(Model model) { 	
		MyUser s = new MyUser();
		model.addAttribute("user", s);		
		return "formNewUser";
	}
	
	//save new user if its email not already exist.
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute MyUser user, HttpServletRequest request, RedirectAttributes attributes) 
			throws MessagingException {
		
		boolean userEmailAlreadyExist = false;
		Optional<MyUser> existingUser = userService.getUserByEmail(user.getEmail());
		if (existingUser.isPresent()) {
			userEmailAlreadyExist = true;
		} else {
    		userService.saveUser(user, getSiteURL(request));
    		userService.activateUser(user, getSiteURL(request), 5);
    	}
	    attributes.addFlashAttribute("userAlreadyExist", userEmailAlreadyExist);
	    return "redirect:/createUser";
	}
	
	//update user only if a drawing is not already linked to another user (OneToOne relation).
	@PostMapping("/updateUser")
	public ModelAndView updateUser(@ModelAttribute MyUser user, @RequestParam("drawingID") int drawingID, 
			RedirectAttributes attributes, HttpServletRequest request) 
			 throws MessagingException {
		
		Optional<Drawing> drawingUser = serviceDrawing.getDrawing(drawingID);
		if (drawingUser.isPresent()) {
			Optional<MyUser> userWithSameDrawing = userService.getUserByDrawingID(drawingID);
			if (userWithSameDrawing.isEmpty() || userWithSameDrawing.get().getId()==user.getId()) {
			user.setDrawing(drawingUser.get()); 
			userService.updateUser(user, getSiteURL(request));
			attributes.addFlashAttribute("message2", "L'utilisateur a bien été modifié.");
			} else {
			attributes.addFlashAttribute("message1", "Modification impossible : la caricature est déjà associée à un autre utilisateur !");
			}
		} else {
		userService.updateUser(user, getSiteURL(request));
		attributes.addFlashAttribute("message2", "L'utilisateur a bien été modifié.");
		}
		return new ModelAndView("redirect:/admin/");
	}
	
	//update newsletterSubscription.
	@PostMapping("/updatNewsletterSubscription")
	public ModelAndView updatNewsletterSubscription(@ModelAttribute MyUser user, RedirectAttributes attributes) {	
		String message = userService.updateMyUserNewsletter(user);
		attributes.addFlashAttribute("message", message);
		return new ModelAndView("redirect:/account");
	}
	 
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}  
	  
	@GetMapping("/updateUser/{id}")
	public String updateUser(@PathVariable("id") final int id, Model model) {   	
		Optional<MyUser> s = userService.getUser(id);
		model.addAttribute("user", s);
		int drawingUserID = 0;
		String drawingUserName = "Aucune";
		if (userService.getUser(id).get().getDrawing()!=null) {
			drawingUserID = userService.getUser(id).get().getDrawing().getId();
			drawingUserName = userService.getUser(id).get().getDrawing().getName();
		}
		model.addAttribute("drawingUserID", drawingUserID);
		model.addAttribute("drawingUserName", drawingUserName);
		Iterable<Drawing> drawings = serviceDrawing.getDrawingsByType("Caricatures");
		model.addAttribute("drawings", drawings);
		
		return "formUpdateUser";		
	}
	
	@GetMapping("/deleteUserPage")
	public String deleteUserConfirmationPage() {
		return "formDeleteUser";		
	}                                               
	
	//delete user by id.
	@PostMapping("/deleteUser/{id}")
	public ModelAndView deleteUser(@PathVariable("id") final int id, HttpServletRequest request) {
		try {
			userService.deleteUser(id);
		} catch (Exception e ) {
			System.out.println("error occuring when deleting user with id :" + id);		
		}
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
	public String resetPassword(@ModelAttribute MyUser user, HttpServletRequest request, RedirectAttributes attributes) 
			throws MessagingException {
		Boolean r = userService.resetPassword(user, getSiteURL(request));
		userService.deleteVerificationCode(user, 3600);
	    attributes.addFlashAttribute("passwordReset", r);
	    return "redirect:/resetPassword";	
	}

	@GetMapping("/admin/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		Iterable<MyUser> listUsers = userService.getUsers();
		UserExcelExporter excelExporter = new UserExcelExporter(listUsers);
		excelExporter.export(response);
	}

}
