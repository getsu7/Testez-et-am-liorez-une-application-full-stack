package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherController — Tests unitaires")
class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/teacher/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Doit retourner 200 avec le DTO quand le professeur existe")
        void shouldReturn200WhenTeacherExists() {
            when(teacherService.findByIdOrThrow(1L)).thenReturn(teacher);
            when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

            ResponseEntity<?> response = teacherController.findById("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(teacherDto);
            verify(teacherService).findByIdOrThrow(1L);
            verify(teacherMapper).toDto(teacher);
        }

        @Test
        @DisplayName("Doit propager ResponseStatusException quand le professeur est introuvable")
        void shouldPropagateExceptionWhenTeacherNotFound() {
            when(teacherService.findByIdOrThrow(99L))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

            assertThatThrownBy(() -> teacherController.findById("99"))
                    .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Doit propager NumberFormatException quand l'ID n'est pas un nombre")
        void shouldThrowNumberFormatExceptionForNonNumericId() {
            assertThatThrownBy(() -> teacherController.findById("abc"))
                    .isInstanceOf(NumberFormatException.class);
            verifyNoInteractions(teacherService);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/teacher
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Doit retourner 200 avec la liste de tous les professeurs")
        void shouldReturn200WithAllTeachers() {
            List<Teacher> teachers = List.of(teacher);
            List<TeacherDto> teacherDtos = List.of(teacherDto);
            when(teacherService.findAll()).thenReturn(teachers);
            when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

            ResponseEntity<?> response = teacherController.findAll();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(teacherDtos);
            verify(teacherService).findAll();
        }

        @Test
        @DisplayName("Doit retourner 200 avec une liste vide quand aucun professeur n'existe")
        void shouldReturn200WithEmptyListWhenNoTeachers() {
            when(teacherService.findAll()).thenReturn(List.of());
            when(teacherMapper.toDto(List.of())).thenReturn(List.of());

            ResponseEntity<?> response = teacherController.findAll();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat((List<?>) response.getBody()).isEmpty();
        }
    }
}
