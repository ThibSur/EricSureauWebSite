package com.websiteSureau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.websiteSureau.model.SiteNews;

@Repository
public interface SiteNewsRepository extends JpaRepository <SiteNews, Integer> {
	
}
