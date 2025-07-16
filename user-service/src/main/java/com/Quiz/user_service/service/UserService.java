package com.Quiz.user_service.service;

import com.Quiz.user_service.dto.UserDto;
import com.Quiz.user_service.exception.UserNotFoundException;
import com.Quiz.user_service.mapper.UserMapper;
import com.Quiz.user_service.model.User;
import com.Quiz.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDto> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(UserMapper::toDto);
    }

    public UserDto findUserByUsername(String username) {
        User user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Le user %s n'existe pas!", username)));
        return UserMapper.toDto(user);
    }

    public UserDto findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Le user %s n'existe pas!", id)));
    return UserMapper.toDto(user);
    }

    public UserDto register(UserDto userDto) {
        if (!isValidPassword(userDto.getPassword())) {
            throw new IllegalArgumentException("Le mot de passe ne respecte pas les critères de sécurité !");
        }

        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    public void updateUser(UserDto userDto) {
        if(userDto.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du user est requis pour la mise à jour.");
        }
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(()-> new UserNotFoundException(String.format("Le user %s n'existe pas!", userDto.getId())));
        mergerUser(user, userDto);
        userRepository.save(user);
    }

    private void mergerUser(User user, UserDto userDto) {
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            if (!isValidPassword(userDto.getPassword())) {
                throw new IllegalArgumentException("Le mot de passe ne respecte pas les critères de sécurité !");
            }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }

    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("Le user %s n'existe pas!", id));
        }
        userRepository.deleteById(id);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }

}
