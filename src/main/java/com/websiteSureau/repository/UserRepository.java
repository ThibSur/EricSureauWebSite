package com.websiteSureau.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.websiteSureau.model.MyUser;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Integer> {
	Optional<MyUser> findByEmail(String email);
    MyUser findByUserName(String userName);
    Iterable<MyUser> findByNewsletterSubscription(boolean subscription);
    Iterable<MyUser> findByAuthority(String role);
    
    @Query(
    	value = "SELECT * FROM users WHERE drawing_id = :id", 
    	nativeQuery = true)
    Optional<MyUser> findByDrawingID(@Param("id") int drawingID);
}