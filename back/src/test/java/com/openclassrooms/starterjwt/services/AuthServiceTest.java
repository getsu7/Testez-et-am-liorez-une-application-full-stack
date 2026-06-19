package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Tests unitaires")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).email("test@example.com")
                .firstName("John").lastName("Doe")
                .password("encoded").admin(false)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        signupRequest = new SignupRequest();
        signupRequest.setEmail("new@example.com");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setPassword("password");

        userDetails = UserDetailsImpl.builder()
                .id(1L).username("test@example.com")
                .firstName("John").lastName("Doe")
                .password("encoded").admin(false)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // authenticateUser()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("authenticateUser()")
    class AuthenticateUser {

        @Test
        @DisplayName("Doit retourner un JwtResponse complet pour un utilisateur standard")
        void shouldReturnFullJwtResponseForStandardUser() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            JwtResponse response = authService.authenticateUser(loginRequest);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("test@example.com");
            assertThat(response.getFirstName()).isEqualTo("John");
            assertThat(response.getLastName()).isEqualTo("Doe");
            assertThat(response.getAdmin()).isFalse();
            verify(authenticationManager).authenticate(any());
            verify(jwtUtils).generateJwtToken(authentication);
            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Doit retourner admin=true pour un administrateur")
        void shouldReturnAdminTrueForAdminUser() {
            User adminUser = User.builder().id(2L).email("admin@example.com")
                    .firstName("Admin").lastName("User").password("enc").admin(true).build();
            UserDetailsImpl adminDetails = UserDetailsImpl.builder()
                    .id(2L).username("admin@example.com")
                    .firstName("Admin").lastName("User").password("enc").admin(true).build();
            LoginRequest adminLogin = new LoginRequest();
            adminLogin.setEmail("admin@example.com");
            adminLogin.setPassword("password");

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminDetails);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("admin-token");
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

            JwtResponse response = authService.authenticateUser(adminLogin);

            assertThat(response.getAdmin()).isTrue();
        }

        @Test
        @DisplayName("Doit retourner admin=false quand l'utilisateur est introuvable en base")
        void shouldReturnAdminFalseWhenUserNotFoundInDb() {
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            JwtResponse response = authService.authenticateUser(loginRequest);

            assertThat(response.getAdmin()).isFalse();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // registerUser()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("registerUser()")
    class RegisterUser {

        @Test
        @DisplayName("Doit créer et retourner l'utilisateur quand l'email est disponible")
        void shouldCreateUserWhenEmailIsAvailable() {
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = authService.registerUser(signupRequest);

            assertThat(result).isNotNull();
            verify(userRepository).existsByEmail("new@example.com");
            verify(passwordEncoder).encode("password");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Doit lever IllegalArgumentException quand l'email est déjà pris")
        void shouldThrowIllegalArgumentExceptionWhenEmailTaken() {
            when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(signupRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Error: Email is already taken!");
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());
        }
    }
}
