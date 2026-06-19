package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController — Tests unitaires")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).email("test@example.com")
                .firstName("John").lastName("Doe")
                .password("encoded").admin(false)
                .build();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setAdmin(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/user/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Doit retourner 200 avec le DTO quand l'utilisateur existe")
        void shouldReturn200WhenUserExists() {
            when(userService.findById(1L)).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userDto);

            ResponseEntity<?> response = userController.findById("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(userDto);
            verify(userService).findById(1L);
            verify(userMapper).toDto(user);
        }

        @Test
        @DisplayName("Doit retourner 404 quand l'utilisateur n'existe pas")
        void shouldReturn404WhenUserNotFound() {
            when(userService.findById(1L)).thenReturn(null);

            ResponseEntity<?> response = userController.findById("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(userService).findById(1L);
            verifyNoInteractions(userMapper);
        }

        @Test
        @DisplayName("Doit propager NumberFormatException quand l'ID n'est pas un nombre")
        void shouldThrowNumberFormatExceptionForNonNumericId() {
            assertThatThrownBy(() -> userController.findById("not-a-number"))
                    .isInstanceOf(NumberFormatException.class);
            verifyNoInteractions(userService);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/user/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("save() [DELETE]")
    class DeleteUser {

        @Test
        @DisplayName("Doit retourner 200 quand la suppression du compte réussit")
        void shouldReturn200OnSuccessfulAccountDeletion() {
            doNothing().when(userService).deleteUserAccount(1L);

            ResponseEntity<?> response = userController.save("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).deleteUserAccount(1L);
        }

        @Test
        @DisplayName("Doit propager ResponseStatusException 404 quand l'utilisateur est introuvable")
        void shouldPropagateNotFoundExceptionWhenUserMissing() {
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                    .when(userService).deleteUserAccount(1L);

            assertThatThrownBy(() -> userController.save("1"))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Doit propager ResponseStatusException 401 quand l'utilisateur tente de supprimer un autre compte")
        void shouldPropagateUnauthorizedExceptionWhenDeletingOtherAccount() {
            doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED))
                    .when(userService).deleteUserAccount(1L);

            assertThatThrownBy(() -> userController.save("1"))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Doit propager NumberFormatException quand l'ID n'est pas un nombre")
        void shouldThrowNumberFormatExceptionForNonNumericId() {
            assertThatThrownBy(() -> userController.save("abc"))
                    .isInstanceOf(NumberFormatException.class);
            verifyNoInteractions(userService);
        }
    }
}
