package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController — Tests unitaires")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        signupRequest = new SignupRequest();
        signupRequest.setEmail("new@example.com");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setPassword("password");

        jwtResponse = new JwtResponse(
                "jwt-token", 1L, "test@example.com", "John", "Doe", false
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/auth/login
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("authenticateUser()")
    class AuthenticateUser {

        @Test
        @DisplayName("Doit retourner 200 avec le JWT quand les identifiants sont valides")
        void shouldReturn200WithJwtOnSuccess() {
            when(authService.authenticateUser(loginRequest)).thenReturn(jwtResponse);

            ResponseEntity<?> response = authController.authenticateUser(loginRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(jwtResponse);
            verify(authService).authenticateUser(loginRequest);
        }

        @Test
        @DisplayName("Doit retourner le JWT d'un administrateur avec admin=true")
        void shouldReturnAdminFlagForAdminUser() {
            JwtResponse adminResponse = new JwtResponse(
                    "admin-token", 2L, "admin@example.com", "Admin", "User", true
            );
            LoginRequest adminRequest = new LoginRequest();
            adminRequest.setEmail("admin@example.com");
            adminRequest.setPassword("adminpass");
            when(authService.authenticateUser(adminRequest)).thenReturn(adminResponse);

            ResponseEntity<?> response = authController.authenticateUser(adminRequest);

            assertThat(((JwtResponse) response.getBody()).getAdmin()).isTrue();
        }

        @Test
        @DisplayName("Doit propager BadCredentialsException quand les identifiants sont invalides")
        void shouldPropagateBadCredentialsException() {
            when(authService.authenticateUser(loginRequest))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authController.authenticateUser(loginRequest))
                    .isInstanceOf(BadCredentialsException.class);
            verify(authService).authenticateUser(loginRequest);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/auth/register
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("registerUser()")
    class RegisterUser {

        @Test
        @DisplayName("Doit retourner 200 avec un message de succès quand l'inscription réussit")
        void shouldReturn200WithSuccessMessage() {
            User newUser = User.builder()
                    .id(1L).email("new@example.com")
                    .firstName("Jane").lastName("Smith")
                    .password("encoded").admin(false)
                    .build();
            when(authService.registerUser(signupRequest)).thenReturn(newUser);

            ResponseEntity<?> response = authController.registerUser(signupRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
            assertThat(((MessageResponse) response.getBody()).getMessage())
                    .isEqualTo("User registered successfully!");
            verify(authService).registerUser(signupRequest);
        }

        @Test
        @DisplayName("Doit retourner 400 quand l'email est déjà pris")
        void shouldReturn400WhenEmailAlreadyTaken() {
            when(authService.registerUser(signupRequest))
                    .thenThrow(new IllegalArgumentException("Error: Email is already taken!"));

            ResponseEntity<?> response = authController.registerUser(signupRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(((MessageResponse) response.getBody()).getMessage())
                    .isEqualTo("Error: Email is already taken!");
            verify(authService).registerUser(signupRequest);
        }

        @Test
        @DisplayName("Doit retourner 400 pour toute IllegalArgumentException")
        void shouldReturn400ForAnyIllegalArgumentException() {
            when(authService.registerUser(signupRequest))
                    .thenThrow(new IllegalArgumentException("Any validation error"));

            ResponseEntity<?> response = authController.registerUser(signupRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
