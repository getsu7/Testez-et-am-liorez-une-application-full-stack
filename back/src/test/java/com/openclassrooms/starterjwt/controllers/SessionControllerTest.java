package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController — Tests unitaires")
class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        Teacher teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();

        session = Session.builder()
                .id(1L).name("Yoga Session").date(new Date())
                .description("A relaxing yoga session")
                .teacher(teacher).users(new ArrayList<>())
                .build();

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("A relaxing yoga session");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(new ArrayList<>());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/session/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Doit retourner 200 avec le DTO quand la session existe")
        void shouldReturn200WhenSessionExists() {
            when(sessionService.getByIdOrThrow(1L)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(sessionDto);

            ResponseEntity<?> response = sessionController.findById("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sessionDto);
            verify(sessionService).getByIdOrThrow(1L);
            verify(sessionMapper).toDto(session);
        }

        @Test
        @DisplayName("Doit propager NumberFormatException quand l'ID n'est pas un nombre")
        void shouldThrowNumberFormatExceptionForNonNumericId() {
            assertThatThrownBy(() -> sessionController.findById("not-a-number"))
                    .isInstanceOf(NumberFormatException.class);
            verifyNoInteractions(sessionService);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/session
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Doit retourner 200 avec la liste de toutes les sessions")
        void shouldReturn200WithAllSessions() {
            List<Session> sessions = List.of(session);
            List<SessionDto> sessionDtos = List.of(sessionDto);
            when(sessionService.findAll()).thenReturn(sessions);
            when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

            ResponseEntity<?> response = sessionController.findAll();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sessionDtos);
            verify(sessionService).findAll();
        }

        @Test
        @DisplayName("Doit retourner 200 avec une liste vide quand aucune session n'existe")
        void shouldReturn200WithEmptyListWhenNoSessions() {
            when(sessionService.findAll()).thenReturn(List.of());
            when(sessionMapper.toDto(List.of())).thenReturn(List.of());

            ResponseEntity<?> response = sessionController.findAll();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat((List<?>) response.getBody()).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/session
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Doit retourner 200 avec la session créée")
        void shouldReturn200WithCreatedSession() {
            when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
            when(sessionService.create(session)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(sessionDto);

            ResponseEntity<?> response = sessionController.create(sessionDto);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sessionDto);
            verify(sessionMapper).toEntity(sessionDto);
            verify(sessionService).create(session);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/session/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Doit retourner 200 avec la session mise à jour")
        void shouldReturn200WithUpdatedSession() {
            when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
            when(sessionService.update(1L, session)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(sessionDto);

            ResponseEntity<?> response = sessionController.update("1", sessionDto);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sessionDto);
            verify(sessionService).update(1L, session);
        }

        @Test
        @DisplayName("Doit propager NumberFormatException quand l'ID n'est pas un nombre")
        void shouldThrowNumberFormatExceptionForNonNumericId() {
            assertThatThrownBy(() -> sessionController.update("abc", sessionDto))
                    .isInstanceOf(NumberFormatException.class);
            verifyNoInteractions(sessionService);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/session/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("save() [DELETE]")
    class Delete {

        @Test
        @DisplayName("Doit retourner 200 quand la suppression réussit")
        void shouldReturn200OnSuccessfulDelete() {
            doNothing().when(sessionService).delete(1L);

            ResponseEntity<?> response = sessionController.save("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(sessionService).delete(1L);
        }

        @Test
        @DisplayName("Doit propager NumberFormatException quand l'ID n'est pas un nombre")
        void shouldThrowNumberFormatExceptionForNonNumericId() {
            assertThatThrownBy(() -> sessionController.save("xyz"))
                    .isInstanceOf(NumberFormatException.class);
            verifyNoInteractions(sessionService);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/session/{id}/participate/{userId}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("participate()")
    class Participate {

        @Test
        @DisplayName("Doit retourner 200 quand la participation réussit")
        void shouldReturn200OnSuccessfulParticipation() {
            doNothing().when(sessionService).participate(1L, 2L);

            ResponseEntity<?> response = sessionController.participate("1", "2");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(sessionService).participate(1L, 2L);
        }

        @Test
        @DisplayName("Doit propager BadRequestException quand l'utilisateur participe déjà")
        void shouldPropagateBadRequestExceptionWhenAlreadyParticipating() {
            doThrow(new BadRequestException()).when(sessionService).participate(1L, 2L);

            assertThatThrownBy(() -> sessionController.participate("1", "2"))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("Doit propager NotFoundException quand la session est introuvable")
        void shouldPropagateNotFoundExceptionWhenSessionMissing() {
            doThrow(new NotFoundException()).when(sessionService).participate(1L, 2L);

            assertThatThrownBy(() -> sessionController.participate("1", "2"))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/session/{id}/participate/{userId}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("noLongerParticipate()")
    class NoLongerParticipate {

        @Test
        @DisplayName("Doit retourner 200 quand la désinscription réussit")
        void shouldReturn200OnSuccessfulWithdrawal() {
            doNothing().when(sessionService).noLongerParticipate(1L, 2L);

            ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(sessionService).noLongerParticipate(1L, 2L);
        }

        @Test
        @DisplayName("Doit propager BadRequestException quand l'utilisateur ne participe pas")
        void shouldPropagateBadRequestExceptionWhenNotParticipating() {
            doThrow(new BadRequestException()).when(sessionService).noLongerParticipate(1L, 2L);

            assertThatThrownBy(() -> sessionController.noLongerParticipate("1", "2"))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("Doit propager NotFoundException quand la session est introuvable")
        void shouldPropagateNotFoundExceptionWhenSessionMissing() {
            doThrow(new NotFoundException()).when(sessionService).noLongerParticipate(1L, 2L);

            assertThatThrownBy(() -> sessionController.noLongerParticipate("1", "2"))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
