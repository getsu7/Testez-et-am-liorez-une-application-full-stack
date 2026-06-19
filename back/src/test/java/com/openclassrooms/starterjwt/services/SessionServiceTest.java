package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService — Tests unitaires")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        Teacher teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        user = User.builder().id(1L).email("user@test.com").firstName("Jane")
                .lastName("Doe").password("pwd").admin(false).build();
        session = Session.builder()
                .id(1L).name("Yoga Session").date(new Date())
                .description("Relaxing session").teacher(teacher)
                .users(new ArrayList<>()).build();
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Doit sauvegarder et retourner la session créée")
        void shouldSaveAndReturnCreatedSession() {
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            Session result = sessionService.create(session);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Yoga Session");
            verify(sessionRepository).save(session);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Doit déléguer la suppression au repository")
        void shouldDelegateDeleteToRepository() {
            doNothing().when(sessionRepository).deleteById(1L);

            sessionService.delete(1L);

            verify(sessionRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Doit retourner toutes les sessions")
        void shouldReturnAllSessions() {
            when(sessionRepository.findAll()).thenReturn(List.of(session));

            List<Session> result = sessionService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Yoga Session");
            verify(sessionRepository).findAll();
        }

        @Test
        @DisplayName("Doit retourner une liste vide si aucune session n'existe")
        void shouldReturnEmptyListWhenNoSessions() {
            when(sessionRepository.findAll()).thenReturn(List.of());

            assertThat(sessionService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("Doit retourner la session quand elle existe")
        void shouldReturnSessionWhenFound() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            assertThat(sessionService.getById(1L)).isEqualTo(session);
        }

        @Test
        @DisplayName("Doit retourner null quand la session est introuvable")
        void shouldReturnNullWhenNotFound() {
            when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(sessionService.getById(99L)).isNull();
        }
    }

    @Nested
    @DisplayName("getByIdOrThrow()")
    class GetByIdOrThrow {

        @Test
        @DisplayName("Doit retourner la session quand elle existe")
        void shouldReturnSessionWhenFound() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            assertThat(sessionService.getByIdOrThrow(1L)).isEqualTo(session);
        }

        @Test
        @DisplayName("Doit lever une exception quand la session est introuvable")
        void shouldThrowExceptionWhenNotFound() {
            when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.getByIdOrThrow(99L))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Doit mettre à jour l'ID de la session et la sauvegarder")
        void shouldSetIdAndSaveSession() {
            session.setName("Updated Yoga");
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            Session result = sessionService.update(1L, session);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(sessionRepository).save(session);
        }
    }

    @Nested
    @DisplayName("participate()")
    class Participate {

        @Test
        @DisplayName("Doit ajouter l'utilisateur à la session")
        void shouldAddUserToSession() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            sessionService.participate(1L, 1L);

            assertThat(session.getUsers()).contains(user);
            verify(sessionRepository).save(session);
        }

        @Test
        @DisplayName("Doit lever NotFoundException quand la session est introuvable")
        void shouldThrowNotFoundExceptionWhenSessionMissing() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                    .isInstanceOf(NotFoundException.class);
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Doit lever NotFoundException quand l'utilisateur est introuvable")
        void shouldThrowNotFoundExceptionWhenUserMissing() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Doit lever BadRequestException si l'utilisateur participe déjà")
        void shouldThrowBadRequestExceptionWhenAlreadyParticipating() {
            session.getUsers().add(user);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                    .isInstanceOf(BadRequestException.class);
            verify(sessionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("noLongerParticipate()")
    class NoLongerParticipate {

        @Test
        @DisplayName("Doit retirer l'utilisateur de la session")
        void shouldRemoveUserFromSession() {
            session.getUsers().add(user);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            sessionService.noLongerParticipate(1L, 1L);

            assertThat(session.getUsers()).doesNotContain(user);
            verify(sessionRepository).save(session);
        }

        @Test
        @DisplayName("Doit lever NotFoundException quand la session est introuvable")
        void shouldThrowNotFoundExceptionWhenSessionMissing() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Doit lever BadRequestException si l'utilisateur ne participe pas")
        void shouldThrowBadRequestExceptionWhenUserNotParticipating() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                    .isInstanceOf(BadRequestException.class);
        }
    }
}
