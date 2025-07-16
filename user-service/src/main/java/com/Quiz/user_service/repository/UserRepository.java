package com.Quiz.user_service.repository;

import com.Quiz.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> getUserByUsername(String username);

}
