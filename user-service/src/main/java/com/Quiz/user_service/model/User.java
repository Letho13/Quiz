package com.Quiz.user_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="my_user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String email;
    private String role;


}
