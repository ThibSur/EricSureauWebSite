package com.websiteSureau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.websiteSureau.model.MyUser;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
	MyUser findByEmail(String email);
    MyUser findByUserName(String userName);

}