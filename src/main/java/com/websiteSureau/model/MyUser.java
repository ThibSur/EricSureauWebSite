package com.websiteSureau.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Data
@Entity
@DynamicUpdate
@Table(name = "users")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String userName;
    
    private String email;
    
    private String password;
    
    private String name;
    
    private String lastName;    

    private String authority;
    
    private int enabled;
    
    @Column(name = "verification_code", length = 64)
    private String verificationCode;
    
    private boolean newsletterSubscription;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drawing_id", referencedColumnName = "id")
    private Drawing drawing;
    
}