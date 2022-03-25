package com.websiteSureau.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CalendarService {
	
	public String[] createArrayWithDateinLetters(ArrayList<String> listOfDate) {
    	
		Map<String, String> monthsInLettersMap = new HashMap<String, String>() ;
		
			monthsInLettersMap.put("01", "Janvier");
			monthsInLettersMap.put("02", "Février");
			monthsInLettersMap.put("03", "Mars");
			monthsInLettersMap.put("04", "Avril");
			monthsInLettersMap.put("05", "Mai");
			monthsInLettersMap.put("06", "Juin");
			monthsInLettersMap.put("07", "Juillet");
			monthsInLettersMap.put("08", "Août");
			monthsInLettersMap.put("09", "Septembre");
			monthsInLettersMap.put("10", "Octobre");
			monthsInLettersMap.put("11", "Novembre");
			monthsInLettersMap.put("12", "Décembre");
		
		String[] dateInLetters = new String [listOfDate.size()];	
		listOfDate.toArray(dateInLetters);
		
		for (int i = 0 ; i < dateInLetters.length ; i++) {
			
			String[] dates = dateInLetters[i].split("-");
			String year = dates[0];
			String month = dates[1];
			
			dateInLetters[i] = monthsInLettersMap.get(month) + " " + year;	
		}
		
		return dateInLetters;
		
	}

}
