package com.websiteSureau.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.repository.DrawingRepository;

@Service
@Transactional
public class DrawingService {

	@Autowired
	private DrawingRepository drawingRepository;
    
    public Iterable<Drawing> getDrawings() {
        return drawingRepository.findAll();
    }
    
    public Optional<Drawing> getDrawing(final int id) {
        return drawingRepository.findById(id);
    }
    
    public Drawing getDrawingByName(String name) {
        return drawingRepository.findByName(name);
    }
    
    public ArrayList<Drawing> getDrawingsByType(String type) {
    	return drawingRepository.findByType(type);
    }
    
	public boolean save(Drawing drawingToSave) throws Exception {
		
		//save drawing in db with a date in YY-MM format
		
		String imageName = drawingToSave.getName();
		if (getDrawingByName(imageName)==null) { 		

	        Calendar cal = Calendar.getInstance();
	        String month = null;
	        int m = cal.get(Calendar.MONTH) + 1;
	        
	        if (cal.get(Calendar.MONTH)==10 || cal.get(Calendar.MONTH)==11) { 
	        	month = m + "";      
	        } else { month = "0" +  m ; }
	        
	        String date = cal.get(Calendar.YEAR) + "-" + month;   
	        
	        Drawing drawing = new Drawing();
	        drawing.setName(imageName);
	        drawing.setDate(date);
	        drawing.setTitle(drawingToSave.getTitle());
	        drawing.setType(drawingToSave.getType());
	        drawing.setPrivateDrawing(drawingToSave.isPrivateDrawing());
	        
	        drawingRepository.save(drawing);
	       
	        return true;
	        
		} else { return false; }
    }
	
	public void updateDrawing(Drawing drawing) {
	
		Drawing currentDrawing = getDrawing(drawing.getId()).get();
		
		String name = drawing.getName();
		if (name != currentDrawing.getName()) {
			currentDrawing.setName(name);
		}
		
	    String type = drawing.getType();
		if (type != currentDrawing.getType()) {
			currentDrawing.setType(type);
		}
		
		String title = drawing.getTitle();
		if (title != currentDrawing.getTitle()) {
			currentDrawing.setTitle(title);
		}
		
		String date = drawing.getDate();
		if (date != currentDrawing.getDate()) {
			String[] dateInter = date.split("-");
			String dateFinal = dateInter[0] + "-" + dateInter[1];
			currentDrawing.setDate(dateFinal);
		}
		
		boolean privateDrawing = drawing.isPrivateDrawing();
		if (privateDrawing != currentDrawing.isPrivateDrawing()) {
			currentDrawing.setPrivateDrawing(privateDrawing);
		}
			
		drawingRepository.save(currentDrawing);
		
	}
	
	public void delete(Drawing d) {
		drawingRepository.deleteById(d.getId());
	}
	
	public String[] getComicsTitles() {
		
		Iterable<Drawing> listDrawings = drawingRepository.findByType("BD");    			
		HashSet<String> hash = new HashSet<String>();  
   	  			
	   	for (Drawing d:listDrawings) {
			hash.add(d.getTitle());
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
