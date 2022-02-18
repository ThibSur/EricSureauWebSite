package com.websiteSureau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.websiteSureau.model.Drawing;

	@Repository
	public interface DrawingRepository extends JpaRepository<Drawing, Integer> {		
	}

