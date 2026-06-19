package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl — Tests unitaires")
class UserDetailsServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).email("test@example.com")
                .firstName("John").lastName("Doe")
                .password("encoded").admin(false)
                .build();
    }

    @Nested
    @DisplayName("loadUserByUsername()")
    class LoadUserByUsername {

        @Test
        @DisplayName("Doit charger et mapper correctement un utilisateur existant")
        void shouldLoadAndMapExistingUser() {
            when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
            assertThat(userDetails.getPassword()).isEqualTo("encoded");

            UserDetailsImpl impl = (UserDetailsImpl) userDetails;
            assertThat(impl.getId()).isEqualTo(1L);
            assertThat(impl.getFirstName()).isEqualTo("John");
            assertThat(impl.getLastName()).isEqualTo("Doe");
            verify(userService).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Note : le flag admin n'est pas mappé par loadUserByUsername")
        void adminFlagIsNotMappedByLoadUserByUsername() {
            // Le service UserDetailsServiceImpl ne mappe pas le champ admin
            // lors de la construction de UserDetailsImpl (cf. code source).
            // Ce test documente ce comportement et sert de signal pour corriger
            // le mapping si ce champ devient nécessaire à la sécurité.
            User adminUser = User.builder()
                    .id(2L).email("admin@example.com")
                    .firstName("Admin").lastName("User")
                    .password("adminPwd").admin(true)
                    .build();
            when(userService.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

            UserDetails userDetails = userDetailsService.loadUserByUsername("admin@example.com");

            // Le service ne transmet pas le flag admin — valeur attendue : null
            assertThat(((UserDetailsImpl) userDetails).getAdmin()).isNull();
        }

        @Test
        @DisplayName("Doit lever UsernameNotFoundException si l'email est introuvable")
        void shouldThrowUsernameNotFoundExceptionWhenEmailNotFound() {
            when(userService.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@example.com"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User Not Found with email: unknown@example.com");
        }

        @Test
        @DisplayName("Doit lever UsernameNotFoundException si l'email est null")
        void shouldThrowUsernameNotFoundExceptionWhenEmailIsNull() {
            when(userService.findByEmail(null)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
