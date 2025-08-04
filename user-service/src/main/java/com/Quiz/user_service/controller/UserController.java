package com.Quiz.user_service.controller;


import com.Quiz.user_service.mapper.UserMapper;
import com.Quiz.user_service.model.User;
import com.Quiz.user_service.repository.UserRepository;
import com.Quiz.user_service.service.UserService;
import com.quiz.shared.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserDto> userDtoPage = userService.findAllUsers(page, size);
        List<UserDto> userDtoList = userDtoPage.getContent();
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Integer id) {
        UserDto userDto = userService.findUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable("id") Integer id,
            @RequestBody @Valid UserDto userDto) {
        userDto.setId(id);
        userService.updateUser(userDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(userService.register(userDto));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
    }

}
