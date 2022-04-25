package com.websiteSureau.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.Drawing;
import com.websiteSureau.repository.DrawingRepository;

@Service
@Primary
@Transactional
public class DrawingService {

	@Autowired
	protected DrawingRepository drawingRepository;
    
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
    
    //save drawing in db with a date in YY-MM format
	public boolean save(Drawing drawingToSave) throws Exception {
		String imageName = drawingToSave.getName();
		if (getDrawingByName(imageName)==null) { 		
	        Calendar cal = Calendar.getInstance();
	        String month;
	        int m = cal.get(Calendar.MONTH) + 1;      
	        if (cal.get(Calendar.MONTH) == Calendar.NOVEMBER || cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
	        	month = m + "";      
	        } else { 
	        	month = "0" +  m ; 
	        }        
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
	
	//update drawing in db
	public void updateDrawing(Drawing drawing) {
		Optional<Drawing> d = getDrawing(drawing.getId());
		if (d.isPresent()) {
			Drawing currentDrawing = d.get();
			String name = drawing.getName();
			if (!currentDrawing.getName().equals(name)) {
				currentDrawing.setName(name);
			}
			String type = drawing.getType();
			if (!currentDrawing.getType().equals(type)) {
				currentDrawing.setType(type);
			}
			String title = drawing.getTitle();
			if (!currentDrawing.getTitle().equals(title)) {
				currentDrawing.setTitle(title);
			}
			String date = drawing.getDate();
			if (!currentDrawing.getDate().equals(date)) {
				String[] dateInter = date.split("-");
				String dateFinal = dateInter[0] + "-" + dateInter[1];
				currentDrawing.setDate(dateFinal);
			}
			boolean privateDrawing = drawing.isPrivateDrawing();
			if (currentDrawing.isPrivateDrawing() != privateDrawing) {
				currentDrawing.setPrivateDrawing(privateDrawing);
			}
			drawingRepository.save(currentDrawing);
		}
	}
	
	public void delete(Drawing d) {
		drawingRepository.deleteById(d.getId());
	}
	
	public String[] getComicsTitles() {		
		Iterable<Drawing> listDrawings = drawingRepository.findByType("BD");    			
		HashSet<String> hash = new HashSet<>();
	   	for (Drawing d:listDrawings) {
			hash.add(d.getTitle());
	   	}
    	return hash.toArray(new String[hash.size()]);
	}
	
	public ArrayList<String> getDrawingsDate(ArrayList<Drawing> listDrawings) {		
		HashSet<String> hash = new HashSet<>();
	   	for (Drawing d:listDrawings) {
			hash.add(d.getDate());
	   		} 
	   	ArrayList<String> dateArray = new ArrayList<>();
	    for (int i=0; i < (hash.size()) ; i++) {	    	
	    	dateArray.add(hash.toArray(new String[hash.size()])[i]);
	    	}
		dateArray.sort(String::compareTo);
	    Collections.reverse(dateArray);
	    
	    return dateArray;   	    
	}

}
