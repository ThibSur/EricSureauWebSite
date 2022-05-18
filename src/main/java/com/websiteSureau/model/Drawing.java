package com.websiteSureau.model;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPrivateDrawing() {
        return privateDrawing;
    }

    public void setPrivateDrawing(boolean privateDrawing) {
        this.privateDrawing = privateDrawing;
    }

    public Set<MyUser> getUsersLike() {
        return usersLike;
    }

    public void setUsersLike(Set<MyUser> usersLike) {
        this.usersLike = usersLike;
    }
}