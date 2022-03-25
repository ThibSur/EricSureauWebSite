package com.websiteSureau.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.websiteSureau.model.Drawing;
import com.websiteSureau.repository.FileAWSS3Repository;

@Service
@Transactional
public class FilesService {
    
    @Autowired
    private FileAWSS3Repository s3Repository;
    
    public S3ObjectInputStream findByName(String fileName) {
       return s3Repository.findByName(fileName);
    }

	public void save(MultipartFile file, Drawing drawingToSave) throws Exception {
		
		// save file in server
			
		if (drawingToSave.getType().equals("BD_Accueil") || drawingToSave.getType().equals("Dessin-du-Mois")) {
			s3Repository.saveFile(file, "staticimages/public/");
		} else {
			s3Repository.saveFile(file, "staticimages/private/");
		}

    }
	
	public void delete(Drawing drawing) {	
		
		if (drawing.getType().equals("BD_Accueil") || drawing.getType().equals("Dessin-du-Mois")) {
			s3Repository.deleteByName("staticimages/public/" + drawing.getName());
		} else { s3Repository.deleteByName("staticimages/private/" + drawing.getName()); }

	}
	

}
