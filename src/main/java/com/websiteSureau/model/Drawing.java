package com.websiteSureau.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Data
@Entity
@DynamicUpdate
@Table(name = "drawings")
public class Drawing {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String name;
            
    private String date;
    
    private String type;
    
    private String title;
    
    private boolean privateDrawing;
    
    @OneToOne(mappedBy = "drawing")
    private MyUser user;
}