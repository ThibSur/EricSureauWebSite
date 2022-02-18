package com.websiteSureau.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;


@Service
public class CalendarService {
	
	public String[] createArrayWithDateinLetters(ArrayList<String> listOfDate) {
    	
		Map<String, String> monthsInLettersMap = new HashMap<String, String>() ;
		
			monthsInLettersMap.put("00", "Janvier");
			monthsInLettersMap.put("01", "Février");
			monthsInLettersMap.put("02", "Mars");
			monthsInLettersMap.put("03", "Avril");
			monthsInLettersMap.put("04", "Mai");
			monthsInLettersMap.put("05", "Juin");
			monthsInLettersMap.put("06", "Juillet");
			monthsInLettersMap.put("07", "Août");
			monthsInLettersMap.put("08", "Septembre");
			monthsInLettersMap.put("09", "Octobre");
			monthsInLettersMap.put("10", "Novembre");
			monthsInLettersMap.put("11", "Décembre");
		
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
