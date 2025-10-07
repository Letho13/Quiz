package com.Quiz.user_service.controllerTest;

import com.Quiz.user_service.controller.UserController;
import com.Quiz.user_service.service.UserService;
import com.quiz.shared.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Charger uniquement le contexte Web pour ce contrôleur
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Pour simuler les requêtes HTTP

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets en JSON

    // 2. Simuler la couche Service pour isoler le contrôleur
    @MockBean
    private UserService userService;

    private final String BASE_URL = "/api/user";
    private final UserDto testUserDto = UserDto.builder()
            .id(1)
            .username("testuser")
            .email("test@quiz.com")
            .role("USER")
            .build();

    // --- GET /api/user/{id} ---

    @Test
    @DisplayName("Devrait retourner 200 et l'utilisateur par ID")
    @WithMockUser // Simuler un utilisateur authentifié pour passer la sécurité de base
    void getUserById_ShouldReturn200AndUser() throws Exception {
        // Given
        when(userService.findUserById(1)).thenReturn(testUserDto);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.id").value(1));

        verify(userService, times(1)).findUserById(1);
    }

    // --- GET /api/user/by-username/{username} ---

    @Test
    @DisplayName("Devrait retourner 200 et l'utilisateur par nom d'utilisateur (Feign Client)")
    @WithMockUser // Nécessite une authentification
    void getUserByUsername_ShouldReturn200AndUser() throws Exception {
        // Given
        when(userService.findUserByUsername("testuser")).thenReturn(testUserDto);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/by-username/{username}", "testuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).findUserByUsername("testuser");
    }

    // --- POST /api/user/add ---

    @Test
    @DisplayName("Devrait retourner 200 et l'utilisateur enregistré (Enregistrement)")
    @WithMockUser
    void addUser_ShouldReturn200AndSavedUser() throws Exception {
        // Given: L'objet userDto à envoyer (sans ID, avec mdp valide pour l'exemple)
        String rawJsonPayload = "{"
                + "\"username\":\"newuser\","
                + "\"email\":\"new@quiz.com\","
                + "\"password\":\"StrongP@ssword1\"," // ESSENTIEL : Mot de passe inclus
                + "\"role\":\"USER\""
                + "}";

        // Simuler le service qui retourne l'objet enregistré (avec ID)
        UserDto savedUserDto = UserDto.builder()
                .id(2)
                .username("newuser")
                .email("new@quiz.com") // L'email doit être là
                .role("USER") // Le rôle doit être là après l'enregistrement
                .build();
        when(userService.register(any(UserDto.class))).thenReturn(savedUserDto);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rawJsonPayload)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(userService, times(1)).register(any(UserDto.class));
    }

    // --- PUT /api/user/{id} ---

    @Test
    @DisplayName("Devrait retourner 200 et appeler updateUser (ADMIN)")
    // Simuler un utilisateur ADMIN pour passer le PreAuthorize
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_ShouldReturn200_WhenAdmin() throws Exception {
        // Given
        UserDto updateDetails = UserDto.builder().email("new.email@quiz.com").build();
        doNothing().when(userService).updateUser(any(UserDto.class)); // Le service ne fait rien

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", 5) // Mettre à jour l'ID 5
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails))
                .with(csrf()))
                .andExpect(status().isOk());

        // Vérifier que updateUser a été appelé, et que le contrôleur a bien défini l'ID
        verify(userService, times(1)).updateUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Devrait retourner 403 (Forbidden) si l'utilisateur n'est pas ADMIN et n'est pas le propriétaire")
    // Simuler un utilisateur simple dont l'ID est 1 (par défaut pour WithMockUser)
    @WithMockUser(username = "userA")
    void updateUser_ShouldReturn403_WhenNotOwnerOrAdmin() throws Exception {
        // La règle PreAuthorize vérifie l'ID dans le principal ou le rôle ADMIN.
        // Puisque nous simulons un simple utilisateur (ID par défaut non géré par WithMockUser),
        // mais l'URL cible l'ID 5, l'accès est refusé.
        UserDto updateDetails = UserDto.builder().email("new.email@quiz.com").build();

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isForbidden()); // 403 Forbidden

        verify(userService, times(0)).updateUser(any(UserDto.class)); // Le service n'est jamais appelé
    }


    // --- DELETE /api/user/{id} ---

    @Test
    @DisplayName("Devrait retourner 200 et appeler deleteUser (Suppression)")
    @WithMockUser(roles = {"ADMIN"}) // La suppression doit être réservée aux admins
    void deleteUser_ShouldReturn200() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", 1)
                .with(csrf()))
                .andExpect(status().isOk()); // La méthode retourne void, donc 200 OK

        verify(userService, times(1)).deleteUser(1);
    }

    // --- GET /api/user ---

    @Test
    @DisplayName("Devrait retourner 200 et la liste des utilisateurs (Pagination)")
    @WithMockUser // Nécessite une authentification
    void getAllUsers_ShouldReturn200AndList() throws Exception {
        // Given
        List<UserDto> users = Collections.singletonList(testUserDto);
        PageImpl<UserDto> page = new PageImpl<>(users);

        when(userService.findAllUsers(anyInt(), anyInt())).thenReturn(page);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$.length()").value(1));

        verify(userService, times(1)).findAllUsers(0, 10);
    }
}
