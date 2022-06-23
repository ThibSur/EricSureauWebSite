package com.websiteSureau.service;

import javax.transaction.Transactional;

import com.websiteSureau.model.DrawingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
		if (type.equals(DrawingType.HOME_COMICS))  {
			s3Repository.saveFile(file, "staticimages/public/");
		} else if (type.equals(DrawingType.MONTH_DRAWING)) {
			s3Repository.saveFile(file, "staticimages/public/");
			s3Repository.saveFile(file, "staticimages/private/");
		} else {
			s3Repository.saveFile(file, "staticimages/private/");
		}
    }
	
	// delete file in server
	public void delete(Drawing drawing) {
		if (drawing.getType().equals(DrawingType.HOME_COMICS) || drawing.getType().equals(DrawingType.MONTH_DRAWING)) {
			s3Repository.deleteByName("staticimages/public/" + drawing.getName());
		} else { s3Repository.deleteByName("staticimages/private/" + drawing.getName()); }

	}
	

}
