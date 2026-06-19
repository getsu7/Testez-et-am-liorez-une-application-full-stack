package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthTokenFilter — Tests unitaires")
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = UserDetailsImpl.builder()
                .id(1L).username("test@example.com")
                .firstName("John").lastName("Doe")
                .password("password").admin(false)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Token valide
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Avec un token valide")
    class WithValidToken {

        @Test
        @DisplayName("Doit authentifier l'utilisateur dans le SecurityContext")
        void shouldAuthenticateUserInSecurityContext() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt");
            when(jwtUtils.validateJwtToken("valid-jwt")).thenReturn(true);
            when(jwtUtils.getUserNameFromJwtToken("valid-jwt")).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

            authTokenFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo(userDetails);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Doit extraire correctement le token du header Authorization")
        void shouldExtractTokenCorrectlyFromHeader() throws ServletException, IOException {
            String jwt = "my-specific-jwt-token";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
            when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
            when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

            authTokenFilter.doFilterInternal(request, response, filterChain);

            verify(jwtUtils).validateJwtToken(jwt);
            verify(jwtUtils).getUserNameFromJwtToken(jwt);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Token invalide / absent
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Avec un token invalide ou absent")
    class WithInvalidOrMissingToken {

        @Test
        @DisplayName("Doit laisser le SecurityContext vide si le token est invalide")
        void shouldNotAuthenticateWithInvalidToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid-jwt");
            when(jwtUtils.validateJwtToken("invalid-jwt")).thenReturn(false);

            authTokenFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(userDetailsService, never()).loadUserByUsername(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Doit laisser le SecurityContext vide si le header Authorization est absent")
        void shouldNotAuthenticateWithoutAuthorizationHeader() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);

            authTokenFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verifyNoInteractions(jwtUtils);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Doit laisser le SecurityContext vide si le header ne commence pas par 'Bearer '")
        void shouldNotAuthenticateWithoutBearerPrefix() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

            authTokenFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verifyNoInteractions(jwtUtils);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Doit laisser le SecurityContext vide si le header Authorization est vide")
        void shouldNotAuthenticateWithEmptyAuthorizationHeader() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("");

            authTokenFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verifyNoInteractions(jwtUtils);
            verify(filterChain).doFilter(request, response);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Gestion des exceptions
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Gestion des exceptions")
    class ExceptionHandling {

        @Test
        @DisplayName("Doit continuer la chaîne de filtres même si une exception est levée")
        void shouldContinueFilterChainEvenOnException() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt");
            when(jwtUtils.validateJwtToken("valid-jwt")).thenReturn(true);
            when(jwtUtils.getUserNameFromJwtToken("valid-jwt")).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com"))
                    .thenThrow(new RuntimeException("User not found"));

            authTokenFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }
}
