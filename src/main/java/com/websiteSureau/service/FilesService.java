package com.websiteSureau.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.websiteSureau.model.Drawing;
import com.websiteSureau.repository.DrawingRepository;
import com.websiteSureau.repository.FileAWSS3Repository;

@Service
public class FilesService {

	@Autowired
	DrawingRepository drawingRepository;
    
    @Autowired
    FileAWSS3Repository s3Repository;
    
    public Iterable<Drawing> getDrawings() {
        return drawingRepository.findAll();
    }
    
    public Optional<Drawing> getDrawing(final int id) {
        return drawingRepository.findById(id);
    }
    
    public S3ObjectInputStream findByName(String fileName) {
       return s3Repository.findByName(fileName);
    }

	public void save(MultipartFile file, String type, String title) throws Exception {
		
		// save file in server
		
		if (type.equals("BD_Accueil") || type.equals("Dessin-du-Mois")) {
			s3Repository.saveFile(file, "staticimages/public/");
		}
		
		else {
			s3Repository.saveFile(file, "staticimages/private/");
		}
		
		//save drawing in db with a date in the YY-MM format
		
		String imageName = file.getOriginalFilename();     		
        Calendar cal = Calendar.getInstance();
        String month = null;
        
        if (cal.get(Calendar.MONTH)==10 || cal.get(Calendar.MONTH)==11) { 
        	month = cal.get(Calendar.MONTH) + "";      
        } else { month= "0" +  cal.get(Calendar.MONTH); }
        
        String date = cal.get(Calendar.YEAR) + "-" + month;   
        
        Drawing drawing = new Drawing();
        drawing.setName(imageName);
        drawing.setDate(date);
        drawing.setTitle(title);
        drawing.setType(type);
        
        drawingRepository.save(drawing);        
    }
	
	public void deleteByName(String name) {
		
		Iterable<Drawing> drawings = getDrawings();
		
		for (Drawing d : drawings) {
			
			// delete file in db	
			if (d.getName().equals(name)) {
				drawingRepository.deleteById(d.getId());
				
				// delete file in server
				if (d.getType().equals("Actualité") || d.getType().equals("Dessin-du-Mois")) {
						s3Repository.deleteByName("staticimages/public/" + name);
				} else { s3Repository.deleteByName("staticimages/private/" + name); }
				
			}
		}
	}
	
	public String[] getComicsTitles() {
		
		Iterable<Drawing> listDrawings = getDrawings();    			
		HashSet<String> hash = new HashSet<String>();  
   	  			
	   	for (Drawing d:listDrawings) {
				if (d.getType().equals("BD")) {
				hash.add(d.getTitle());
	    		}
	   	}
	   	
    	return hash.toArray(new String[hash.size()]);
	}
	
	public ArrayList<String> getDrawingsDate(ArrayList<Drawing> listDrawings) {
		
		HashSet<String> hash = new HashSet<String>();  
   	  			
	   	for (Drawing d:listDrawings) {
			hash.add(d.getDate());
	   	}
	   
	   	ArrayList<String> dateArray = new ArrayList<String>();
	    
	    for (int i=0; i < (hash.size()) ; i++) {	    	
	    	dateArray.add(hash.toArray(new String[hash.size()])[i]);
	    }
	    	    
	    dateArray.sort((d1,d2) -> d1.compareTo(d2));
	    Collections.reverse(dateArray);
	    
	   return dateArray;   
	   	
	}

}
