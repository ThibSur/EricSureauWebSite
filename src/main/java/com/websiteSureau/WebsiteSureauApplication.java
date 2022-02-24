package com.websiteSureau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
*
* webapp to save and display drawings, caricatures, comics and news on the website ericsureau.fr.
* Most of the drawings are only accessible after registration (springsecurity)
*
* @author  Thibault Sureau
* @version 1.0
* @since   2021-12-10
*
**/

@SpringBootApplication
public class WebsiteSureauApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsiteSureauApplication.class, args);
	}

}
