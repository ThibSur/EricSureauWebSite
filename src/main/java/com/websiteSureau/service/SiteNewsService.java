package com.websiteSureau.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websiteSureau.model.SiteNews;
import com.websiteSureau.repository.SiteNewsRepository;

import lombok.Data;

@Data
@Service
@Transactional
public class SiteNewsService {
	
    @Autowired
	private SiteNewsRepository newsRepository;
	
    public List<SiteNews> getNews() {
        return newsRepository.findAll();
    }
    
    public List<SiteNews> getNewsByPrivateNewsEqualFalse() {
    	return newsRepository.findByPrivateNews(false);
    }
    
    public Optional<SiteNews> getNewsById(int id){
    	return newsRepository.findById(id);
    }
    
    public void deleteNews(final int id) {
    	newsRepository.deleteById(id);
    }

    public void addNews(SiteNews news) {
    	if (getNewsById(news.getId()).isEmpty()) {
    		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM");
		    s.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"))); 
		    String date = s.format(System.currentTimeMillis());
	    	news.setDate(date);    		
	    }
    	newsRepository.save(news);
    }
    
	public ArrayList<String> getNewsDate(List<SiteNews> listSiteNews) {
		HashSet<String> hash = new HashSet<>();
	   	for (SiteNews sn:listSiteNews) {
			hash.add(sn.getDate());
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
