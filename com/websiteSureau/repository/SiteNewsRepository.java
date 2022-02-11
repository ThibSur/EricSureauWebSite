package com.websiteSureau.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.websiteSureau.model.SiteNews;

public interface SiteNewsRepository extends JpaRepository <SiteNews, Integer> {
	
}
