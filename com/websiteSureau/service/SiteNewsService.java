package com.websiteSureau.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.SiteNews;
import com.websiteSureau.repository.SiteNewsRepository;

import lombok.Data;

@Data
@Service
public class SiteNewsService {
	
    @Autowired
	private SiteNewsRepository newsRepository;
	
    public Iterable<SiteNews> getNews() {
        return newsRepository.findAll();
    }
    
    public void deleteNews(final int id) {
    	newsRepository.deleteById(id);
    }

    public void addNews(SiteNews news) {
    	
    	String month = null;
        
    	Calendar cal = Calendar.getInstance();
    	 
    	if (cal.get(Calendar.MONTH)==10 || cal.get(Calendar.MONTH)==11) { month = cal.get(Calendar.MONTH) + "";
    	} else { month= "0" +  cal.get(Calendar.MONTH);}
    
    	String date = cal.get(Calendar.YEAR) + "-" + month;
    
    	news.setDate(date);
    	newsRepository.save(news);
    }
    
	public ArrayList<String> getNewsDate(Iterable<SiteNews> listSiteNews) {
		
		HashSet<String> hash = new HashSet<String>();  
   	  			
	   	for (SiteNews sn:listSiteNews) {
			hash.add(sn.getDate());
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
