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
    
    // save file in server
	public void save(MultipartFile file, Drawing drawingToSave) throws Exception {		
		String type = drawingToSave.getType();
		switch (type) {
			case "BD_Accueil":
				s3Repository.saveFile(file, "staticimages/public/");
				break;
			case "Dessin-du-Mois":
				s3Repository.saveFile(file, "staticimages/public/");
				s3Repository.saveFile(file, "staticimages/private/");
				break;
			default:
				s3Repository.saveFile(file, "staticimages/private/");
		}

    }
	
	// delete file in server
	public void delete(Drawing drawing) {		
		if (drawing.getType().equals("BD_Accueil") || drawing.getType().equals("Dessin-du-Mois")) {
			s3Repository.deleteByName("staticimages/public/" + drawing.getName());
		} else { s3Repository.deleteByName("staticimages/private/" + drawing.getName()); }

	}
	

}
