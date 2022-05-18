package com.websiteSureau.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.model.DrawingType;
import com.websiteSureau.model.MyUser;
import com.websiteSureau.repository.FileAWSS3Repository;
import com.websiteSureau.service.DrawingService;
import com.websiteSureau.service.DrawingServiceAdditionalFunctions;
import com.websiteSureau.service.FilesService;
import com.websiteSureau.service.UserService;

@Controller
public class FilesDrawingController {

	@Autowired
	private FilesService fileService;
	
	@Autowired
	private DrawingService drawingService;
	
	@Autowired
	private DrawingServiceAdditionalFunctions drawingService2;
	
	@Autowired
	private UserService userService;
	
	private static final Logger LOG = LoggerFactory.getLogger(FileAWSS3Repository.class);
	    
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, @ModelAttribute Drawing drawing, RedirectAttributes attributes) throws Exception {
    	
    	if (file.isEmpty()) {
            attributes.addFlashAttribute("message1", "Merci de sélectionner une image à sauvegarder.");
		} else {
    		drawing.setName(file.getOriginalFilename());
    		boolean saveSuccessful = drawingService.save(drawing);
	   		if (saveSuccessful) {
	   			fileService.save(file, drawing);
	   			attributes.addFlashAttribute("message2", "L'image " + file.getOriginalFilename() + " a bien été sauvegardée !"); 
	   		} else { 
	   			attributes.addFlashAttribute("message1", "L'image " + file.getOriginalFilename() + " existe déjà !"); 
	   			}
		}
		return "redirect:/admin";
	}
    
    @GetMapping("/getFile")
    public ResponseEntity<Object> findByNamePrivate(@RequestParam String name) {
    	try {
	        return ResponseEntity
	                .ok()
	                .cacheControl(CacheControl.noCache())
	                .header("Content-type", "application/octet-stream")
	                .header("Content-disposition", "attachment; filename=\"" + name + "\"")
	                .body(new InputStreamResource(fileService.findByName("staticimages/private/" + name)));
    	} catch (Exception e) {
    		LOG.error("Error occurred while downloading the file, " + name + " does not exist");
    		return null;
    	}
    }
    
    @GetMapping("/getFileP")
    public ResponseEntity<Object> findByNamePublic(@RequestParam String name) {
    	try {
	        return ResponseEntity
	                .ok()
	                .cacheControl(CacheControl.noCache())
	                .header("Content-type", "application/octet-stream")
	                .header("Content-disposition", "attachment; filename=\"" + name + "\"")
	                .body(new InputStreamResource(fileService.findByName("staticimages/public/" + name)));
	    } catch (Exception e) {
			LOG.error("Error occurred while downloading the file, " + name + " does not exist");
			return null;
		}
    }
    
    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("nameFile") String fileName, RedirectAttributes attributes) {
    	Drawing drawing = drawingService.getDrawingByName(fileName);
    	Optional<MyUser> userLinkedToDrawing = userService.getUserByDrawingID(drawing.getId());
    	if (userLinkedToDrawing.isEmpty()) {
	    	drawingService.delete(drawing);
	    	fileService.delete(drawing);
	    	attributes.addFlashAttribute("message2", "L'image " + fileName + " a été supprimée.");
    	} else { attributes.addFlashAttribute("message1", "Suppression impossible : l'image " + fileName + " est associée à un utilisateur."); }
    	return "redirect:/admin"; 	
    }
    
    @GetMapping("/deleteFilePage")
    public String getDeleteFilePage(@RequestParam("nameFile") String fileName, Model model, RedirectAttributes attributes) {
    	if (fileName.isEmpty()) {
    		attributes.addFlashAttribute("message1", "Veuillez sélectionner une image à supprimer.");
    		return "redirect:/admin";
    	} else {
	    	Drawing drawing = drawingService.getDrawingByName(fileName);
	    	model.addAttribute("drawing", drawing);
		    return "formDeleteDrawing";
    	}
    }
    
    @GetMapping("/drawingUpdatePage")
	public String updateDrawing(@RequestParam("nameFile") String fileName, RedirectAttributes attributes, Model model) {

    	if (fileName.isEmpty()) {
    		attributes.addFlashAttribute("message1", "Veuillez sélectionner une image à modifier.");
        	return "redirect:/admin";
    	} else {
			Drawing dr = drawingService.getDrawingByName(fileName);
			dr.setDate(dr.getDate() + "-" + "01");
			model.addAttribute("drawing", dr);
			model.addAttribute("drawingTypes", DrawingType.TYPES);
			return "formUpdateDrawing";	
    	}
	}
    
    @PostMapping("/updateDrawing")
   	public String updateDrawing(@RequestParam("file") MultipartFile file, @ModelAttribute Drawing drawing, RedirectAttributes attributes) throws Exception { 

    	Drawing oldDrawing = drawingService.getDrawing(drawing.getId()).get();
    	String messageOk = "L'image " + drawing.getName() + " a été modifiée.";
    	
    	if (!file.isEmpty()) {
    		fileService.delete(oldDrawing);
    		drawing.setName(file.getOriginalFilename());
    		drawingService.updateDrawing(drawing);
    		fileService.save(file, drawing);
    		attributes.addFlashAttribute("message2", messageOk);
    		}
    	else {
    		if (drawing.getType().equals(oldDrawing.getType())) {
    			attributes.addFlashAttribute("message2", messageOk);
    			drawingService.updateDrawing(drawing);
    		} else if (drawing.getType().equals(DrawingType.MONTH_DRAWING)) {
    			attributes.addFlashAttribute("message1", "Modification impossible : veuillez sélectionner un fichier pour ajouter un dessin du mois.");
			} else if (drawing.getType().equals(DrawingType.HOME_COMICS)) {
				attributes.addFlashAttribute("message1", "Modification impossible : veuillez sélectionner un fichier pour ajouter une bd d'accueil.");
			} else if (oldDrawing.getType().equals(DrawingType.MONTH_DRAWING)) {
    			fileService.delete(oldDrawing);
    			attributes.addFlashAttribute("message2", messageOk);
    			drawingService.updateDrawing(drawing);
    		} else {
    			attributes.addFlashAttribute("message2", messageOk);
    			drawingService.updateDrawing(drawing);
    		}
    	}
    	
    	return "redirect:/admin";
    }
    
    @PostMapping("/addDrawingLike")
    public String addDrawingLike(@RequestParam("nameFile") String fileName) {
    	drawingService2.addLikeToDrawing(fileName, SecurityContextHolder.getContext().getAuthentication().getName());
    	return "redirect:/"; 	
    }

}
