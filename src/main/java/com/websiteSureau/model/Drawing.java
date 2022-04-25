package com.websiteSureau.model;

import lombok.Data;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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

    @ManyToMany
    @JoinTable(
    		  name = "drawing_like", 
    		  joinColumns = @JoinColumn(name = "drawing_id"), 
    		  inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<MyUser> usersLike;
}