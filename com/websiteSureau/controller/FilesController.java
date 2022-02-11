package com.websiteSureau.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websiteSureau.service.FileDrawingService;

@Controller
public class FilesController {

	@Autowired
	FileDrawingService serviceDrawing;
	    
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("typeDrawing") 
    						String drawingType, @RequestParam("title") String drawingTitle, RedirectAttributes attributes) throws Exception {
    	
    	if (file.isEmpty()) {
            attributes.addFlashAttribute("message1", "Merci de sélectionner une image à sauvegarder.");
            return "redirect:/admin";
    	}
   	 	
    	else {
	   		 serviceDrawing.save(file, drawingType, drawingTitle);
	   		 attributes.addFlashAttribute("message2", "L'image " + file.getOriginalFilename() + " a bien été sauvegardée !"); 
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
                .body(new InputStreamResource(serviceDrawing.findByName("staticimages/private/" + name)));
    }
    
    @GetMapping("/imagesP")
    public ResponseEntity<Object> findByNamePublic(@RequestParam String name) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noCache())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + name + "\"")
                .body(new InputStreamResource(serviceDrawing.findByName("staticimages/public/" + name)));
    }
    
    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("nameFile") String fileName, RedirectAttributes attributes) {
    	
    	if (fileName.isEmpty()) {
    		attributes.addFlashAttribute("message4", "Veuillez sélectionner une image à supprimer");
        	return "redirect:/admin";
    	}
    	
    	else {
	    	serviceDrawing.deleteByName(fileName);
	    	attributes.addFlashAttribute("message3", "L'image " + fileName + " a été supprimée.");
	    	return "redirect:/admin";
    	}
    	
    }
    
}
