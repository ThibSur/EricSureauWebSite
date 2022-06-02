package com.websiteSureau.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.websiteSureau.model.UserConnection;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, Integer> {
    Iterable<UserConnection> findAllByOrderByMonth();
    Optional<UserConnection> findByMonth(String month);
}
