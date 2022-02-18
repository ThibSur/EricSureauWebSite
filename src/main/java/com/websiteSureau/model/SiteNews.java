package com.websiteSureau.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "news")
public class SiteNews {
	
    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private int id;
    
	private String newsTitle;
	
	@Column(length = 800)
	private String texte;
	
	private String date;

}
