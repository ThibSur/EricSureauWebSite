package com.websiteSureau.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.service.DrawingService;
import com.websiteSureau.service.FilesService;

@Controller
public class FilesDrawingController {

	@Autowired
	private FilesService fileService;
	
	@Autowired
	private DrawingService drawingService;
	    
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, @ModelAttribute Drawing drawing, RedirectAttributes attributes) throws Exception {
    	
    	if (file.isEmpty()) {
            attributes.addFlashAttribute("message1", "Merci de sélectionner une image à sauvegarder.");
            return "redirect:/admin";
    	} else {
    		drawing.setName(file.getOriginalFilename());
    		boolean saveSuccessful = drawingService.save(drawing);
	   		if (saveSuccessful==true) {
	   			fileService.save(file, drawing);
	   			attributes.addFlashAttribute("message2", "L'image " + file.getOriginalFilename() + " a bien été sauvegardée !"); 
	   		} else { 
	   			attributes.addFlashAttribute("message1", "L'image " + file.getOriginalFilename() + " existe déjà !"); 
	   			}
	   	    return "redirect:/admin";
   	     }
    }
    
    @GetMapping("/images")
    public ResponseEntity<Object> findByNamePrivate(@RequestParam String name) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noCache())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + name + "\"")
                .body(new InputStreamResource(fileService.findByName("staticimages/private/" + name)));
    }
    
    @GetMapping("/imagesP")
    public ResponseEntity<Object> findByNamePublic(@RequestParam String name) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noCache())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + name + "\"")
                .body(new InputStreamResource(fileService.findByName("staticimages/public/" + name)));
    }
    
    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("nameFile") String fileName, RedirectAttributes attributes) throws UnsupportedEncodingException, MessagingException {
    	
    	Drawing drawing = drawingService.getDrawingByName(fileName);
    	if (drawing.getUser().equals(null)) {
    	drawingService.delete(drawing);
    	fileService.delete(drawing);
    	attributes.addFlashAttribute("message3", "L'image " + fileName + " a été supprimée.");
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
			return "formUpdateDrawing";	
    	}
	}
    
    @PostMapping("/updateDrawing")
   	public String updateDrawing(@RequestParam("file") MultipartFile file, @ModelAttribute Drawing drawing, RedirectAttributes attributes) throws Exception { 

    	Drawing oldDrawing = drawingService.getDrawing(drawing.getId()).get();
    	
    	if (!file.isEmpty()) {
    		fileService.delete(oldDrawing);
    		drawing.setName(file.getOriginalFilename());
    		drawingService.updateDrawing(drawing);
    		fileService.save(file, drawing);
    		attributes.addFlashAttribute("message2", "L'image " + drawing.getName() + " a été modifiée.");
    		}
    	else {
    		if (drawing.getType().equals(oldDrawing.getType())) {
    			attributes.addFlashAttribute("message2", "L'image " + drawing.getName() + " a été modifiée.");
    			drawingService.updateDrawing(drawing);
    		} else {
    			attributes.addFlashAttribute("message1", "Modification impossible : veuillez sélectionner un fichier pour changer de type.");
    		}
    	}
    	
    	return "redirect:/admin";
    }

}