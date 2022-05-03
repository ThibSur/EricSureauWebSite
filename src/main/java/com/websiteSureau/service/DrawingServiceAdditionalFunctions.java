package com.websiteSureau.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.websiteSureau.model.Drawing;
import com.websiteSureau.model.MyUser;

@Service
@Transactional
public class DrawingServiceAdditionalFunctions extends DrawingService {
	
	@Autowired
	private UserService userService;

	//add a like to a drawing
	public void addLikeToDrawing(String drawingName, String userName) {
		try {
			Drawing drawing = getDrawingByName(drawingName);
			Set<MyUser> likes = drawing.getUsersLike();
			MyUser user = userService.getUserByUserName(userName);
			likes.add(user);
			drawing.setUsersLike(likes);
			drawingRepository.save(drawing);
		} catch (Exception e) {
			System.out.println("Error : impossible to add a like");
		}
	}
}


