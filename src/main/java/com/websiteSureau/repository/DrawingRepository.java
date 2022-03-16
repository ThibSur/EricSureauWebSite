package com.websiteSureau.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.websiteSureau.model.Drawing;

@Repository
public interface DrawingRepository extends JpaRepository<Drawing, Integer> {
	ArrayList<Drawing> findByType(String type);
	Drawing findByName(String name);
}

