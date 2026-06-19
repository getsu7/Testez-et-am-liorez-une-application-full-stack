package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService — Tests unitaires")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Doit retourner la liste de tous les enseignants")
        void shouldReturnAllTeachers() {
            when(teacherRepository.findAll()).thenReturn(List.of(teacher));

            List<Teacher> result = teacherService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFirstName()).isEqualTo("John");
            verify(teacherRepository).findAll();
        }

        @Test
        @DisplayName("Doit retourner une liste vide si aucun enseignant n'existe")
        void shouldReturnEmptyListWhenNoTeachers() {
            when(teacherRepository.findAll()).thenReturn(List.of());

            assertThat(teacherService.findAll()).isEmpty();
            verify(teacherRepository).findAll();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Doit retourner l'enseignant quand il existe")
        void shouldReturnTeacherWhenFound() {
            when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

            Teacher result = teacherService.findById(1L);

            assertThat(result).isNotNull().isEqualTo(teacher);
            verify(teacherRepository).findById(1L);
        }

        @Test
        @DisplayName("Doit retourner null quand l'enseignant est introuvable")
        void shouldReturnNullWhenNotFound() {
            when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(teacherService.findById(99L)).isNull();
        }
    }

    @Nested
    @DisplayName("findByIdOrThrow()")
    class FindByIdOrThrow {

        @Test
        @DisplayName("Doit retourner l'enseignant quand il existe")
        void shouldReturnTeacherWhenFound() {
            when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

            Teacher result = teacherService.findByIdOrThrow(1L);

            assertThat(result).isNotNull().isEqualTo(teacher);
            verify(teacherRepository).findById(1L);
        }

        @Test
        @DisplayName("Doit lever ResponseStatusException 404 quand l'enseignant est introuvable")
        void shouldThrowResponseStatusExceptionWhenNotFound() {
            when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> teacherService.findByIdOrThrow(99L))
                    .isInstanceOf(ResponseStatusException.class);
            verify(teacherRepository).findById(99L);
        }
    }
}
