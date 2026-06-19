package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — Tests unitaires")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

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
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Doit retourner l'utilisateur quand il existe")
        void shouldReturnUserWhenFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            User result = userService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Doit retourner null quand l'utilisateur est introuvable")
        void shouldReturnNullWhenNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(userService.findById(99L)).isNull();
        }
    }

    @Nested
    @DisplayName("findByEmail()")
    class FindByEmail {

        @Test
        @DisplayName("Doit retourner l'Optional de l'utilisateur quand l'email existe")
        void shouldReturnUserWhenEmailFound() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            Optional<User> result = userService.findByEmail("test@example.com");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Doit retourner Optional.empty() quand l'email est introuvable")
        void shouldReturnEmptyOptionalWhenEmailNotFound() {
            when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

            assertThat(userService.findByEmail("missing@example.com")).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail()")
    class ExistsByEmail {

        @Test
        @DisplayName("Doit retourner true si l'email existe")
        void shouldReturnTrueWhenEmailExists() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThat(userService.existsByEmail("test@example.com")).isTrue();
        }

        @Test
        @DisplayName("Doit retourner false si l'email est inexistant")
        void shouldReturnFalseWhenEmailNotExists() {
            when(userRepository.existsByEmail("missing@example.com")).thenReturn(false);

            assertThat(userService.existsByEmail("missing@example.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Doit déléguer la suppression au repository par ID")
        void shouldDelegateDeleteToRepository() {
            doNothing().when(userRepository).deleteById(1L);

            userService.delete(1L);

            verify(userRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Doit sauvegarder et retourner l'utilisateur créé")
        void shouldSaveAndReturnCreatedUser() {
            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = userService.create(user);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("deleteUserAccount()")
    class DeleteUserAccount {

        private UserDetails buildUserDetails(String email) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(email).password("p").authorities("ROLE_USER").build();
        }

        @Test
        @DisplayName("Doit supprimer le compte quand l'utilisateur authentifié est le propriétaire")
        void shouldDeleteAccountWhenOwnerAuthenticated() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(buildUserDetails("test@example.com"));
            SecurityContextHolder.setContext(securityContext);
            doNothing().when(userRepository).deleteById(1L);

            userService.deleteUserAccount(1L);

            verify(userRepository).findById(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Doit lever ResponseStatusException 404 quand l'utilisateur est introuvable")
        void shouldThrowNotFoundExceptionWhenUserMissing() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUserAccount(1L))
                    .isInstanceOf(ResponseStatusException.class);
            verify(userRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Doit lever ResponseStatusException 401 quand l'utilisateur tente de supprimer un autre compte")
        void shouldThrowUnauthorizedExceptionWhenDeletingOtherAccount() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(buildUserDetails("other@example.com"));
            SecurityContextHolder.setContext(securityContext);

            assertThatThrownBy(() -> userService.deleteUserAccount(1L))
                    .isInstanceOf(ResponseStatusException.class);
            verify(userRepository, never()).deleteById(any());
        }
    }
}
