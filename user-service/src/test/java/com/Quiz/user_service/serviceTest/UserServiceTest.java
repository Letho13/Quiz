package com.Quiz.user_service.serviceTest;

import com.Quiz.user_service.exception.UserNotFoundException;
import com.Quiz.user_service.model.User;
import com.Quiz.user_service.repository.UserRepository;
import com.Quiz.user_service.service.UserService;
import com.quiz.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // Utilisateur de test
    private User testUser;
    private UserDto testUserDto;
    private final String VALID_PASSWORD = "StrongP@ssword1";

    @BeforeEach
    void setUp() {
        // Initialisation des objets de test pour chaque méthode
        testUser = User.builder()
                .id(1)
                .username("testUser")
                .email("test@example.com")
                .password("encodedPassword")
                .role("USER")
                .build();

        testUserDto = UserDto.builder()
                .id(1)
                .username("testUser")
                .email("test@example.com")
                .password(VALID_PASSWORD)
                .role("USER")
                .build();

        // Réinitialiser le contexte de sécurité avant chaque test
        SecurityContextHolder.clearContext();
    }

    // --- Tests pour findAllUsers ---

    @Test
    @DisplayName("Devrait retourner une Page de UserDto pour findAllUsers")
    void findAllUsers_ShouldReturnPageOfUserDto() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = List.of(testUser);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDto> result = userService.findAllUsers(0, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("testUser", result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findAll(pageable);
    }

    // --- Tests pour findUserByUsername ---

    @Test
    @DisplayName("Devrait trouver un utilisateur par nom d'utilisateur")
    void findUserByUsername_ShouldReturnUserDto_WhenUserExists() {
        // Given
        when(userRepository.getUserByUsername(anyString())).thenReturn(Optional.of(testUser));

        // When
        UserDto result = userService.findUserByUsername("testUser");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("Devrait lancer UserNotFoundException lorsque le nom d'utilisateur n'existe pas")
    void findUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.getUserByUsername(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername("unknown"));
    }

    // --- Tests pour findUserById ---

    @Test
    @DisplayName("Devrait trouver un utilisateur par ID")
    void findUserById_ShouldReturnUserDto_WhenUserExists() {
        // Given
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));

        // When
        UserDto result = userService.findUserById(1);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    @DisplayName("Devrait lancer UserNotFoundException lorsque l'ID n'existe pas")
    void findUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(99));
    }

    // --- Tests pour register ---

    @Test
    @DisplayName("Devrait enregistrer un nouvel utilisateur avec succès et encoder le mot de passe")
    void register_ShouldSaveUserWithEncodedPassword_AndDefaultRole() {
        // Given
        UserDto newUserDto = UserDto.builder()
                .username("newUser")
                .email("new@example.com")
                .password(VALID_PASSWORD)
                .build(); // Pas de rôle

        User savedUser = User.builder()
                .id(2)
                .username("newUser")
                .email("new@example.com")
                .password("encoded_new_password")
                .role("USER")
                .build();

        when(passwordEncoder.encode(newUserDto.getPassword())).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserDto result = userService.register(newUserDto);

        // Then
        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("USER", result.getRole()); // Vérifie l'attribution du rôle par défaut
        verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Devrait lancer IllegalArgumentException si le mot de passe n'est pas valide lors de l'enregistrement")
    void register_ShouldThrowException_WhenPasswordIsInvalid() {
        // Given
        UserDto invalidUserDto = UserDto.builder()
                .username("fail")
                .password("weak") // Mot de passe non valide
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.register(invalidUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- Tests pour updateUser ---

    @Test
    @DisplayName("Devrait mettre à jour le nom d'utilisateur et l'email avec succès")
    void updateUser_ShouldUpdateUsernameAndEmail() {
        // Given
        UserDto updateDto = UserDto.builder()
                .id(1)
                .username("updatedUser")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser)); // Utilisateur existant
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUser(updateDto);

        // Then
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("updatedUser", testUser.getUsername());
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals("encodedPassword", testUser.getPassword()); // Le mot de passe ne devrait pas changer
    }

    @Test
    @DisplayName("Devrait mettre à jour le mot de passe si valide")
    void updateUser_ShouldUpdatePassword_WhenValid() {
        // Given
        UserDto updateDto = UserDto.builder()
                .id(1)
                .password(VALID_PASSWORD)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn("new_encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUser(updateDto);

        // Then
        verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
        assertEquals("new_encoded_password", testUser.getPassword());
    }

    @Test
    @DisplayName("Devrait lancer IllegalArgumentException si ID est nul")
    void updateUser_ShouldThrowException_WhenIdIsNull() {
        // Given
        UserDto updateDto = UserDto.builder().build(); // ID est nul

        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(updateDto));
        verify(userRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Devrait lancer UserNotFoundException si l'utilisateur à mettre à jour n'existe pas")
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        UserDto updateDto = UserDto.builder().id(99).build();
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(updateDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Devrait autoriser l'ADMIN à changer le rôle")
    void updateUser_ShouldAllowAdminToChangeRole() {
        // Given
        UserDto updateDto = UserDto.builder().id(1).role("ADMIN").build();

        // Simuler un contexte de sécurité avec un utilisateur ADMIN
        Authentication auth = mock(Authentication.class);
        GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        List<GrantedAuthority> authorities = Collections.singletonList(adminAuthority);
        when(auth.getAuthorities()).thenReturn((Collection) authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUser(updateDto);

        // Then
        assertEquals("ADMIN", testUser.getRole()); // Le rôle a été changé
    }

    @Test
    @DisplayName("Devrait ignorer la tentative de changement de rôle pour un utilisateur non-ADMIN")
    void updateUser_ShouldIgnoreRoleChangeForNonAdmin() {
        // Given
        testUser.setRole("USER"); // S'assurer que le rôle de départ n'est pas ADMIN
        UserDto updateDto = UserDto.builder().id(1).role("ADMIN").build();

        // Simuler un contexte de sécurité avec un utilisateur simple
        Authentication auth = mock(Authentication.class);
        GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        when(auth.getAuthorities()).thenReturn((Collection) Collections.singletonList(userAuthority));
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUser(updateDto);

        // Then
        assertEquals("USER", testUser.getRole()); // Le rôle est resté 'USER'
    }

    // --- Tests pour deleteUser ---

    @Test
    @DisplayName("Devrait supprimer l'utilisateur si il existe")
    void deleteUser_ShouldDelete_WhenUserExists() {
        // Given
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        // When
        userService.deleteUser(1);

        // Then
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Devrait lancer UserNotFoundException si l'utilisateur à supprimer n'existe pas")
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(99)).thenReturn(false);

        // Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99));
        verify(userRepository, never()).deleteById(anyInt());
    }

    // --- Tests pour loadUserByUsername ---

    @Test
    @DisplayName("Devrait charger les détails de l'utilisateur pour Spring Security")
    void loadUserByUsername_ShouldReturnUserDetails() {
        // Given
        when(userRepository.getUserByUsername("testUser")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userService.loadUserByUsername("testUser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Devrait lancer UsernameNotFoundException si l'utilisateur n'est pas trouvé par Spring Security")
    void loadUserByUsername_ShouldThrowException_WhenNotFound() {
        // Given
        when(userRepository.getUserByUsername("unknown")).thenReturn(Optional.empty());

        // Then
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown"));
    }

    // --- Tests pour searchUsers ---

    @Test
    @DisplayName("Devrait retourner une Page de UserDto lors de la recherche")
    void searchUsers_ShouldReturnPageOfUserDto() {
        // Given
        String query = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = List.of(testUser);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                eq(query), eq(query), eq(pageable))).thenReturn(userPage);

        // When
        Page<UserDto> result = userService.searchUsers(query, 0, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals("testUser", result.getContent().get(0).getUsername());
        verify(userRepository, times(1))
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable);
    }
}
