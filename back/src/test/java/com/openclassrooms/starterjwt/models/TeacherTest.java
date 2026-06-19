package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires du modèle Teacher.
 * On teste uniquement le comportement métier (égalité par ID, Accessors chain).
 */
@DisplayName("Teacher — Tests unitaires du modèle")
class TeacherTest {

    @Nested
    @DisplayName("equals() et hashCode() — basés sur l'ID")
    class EqualityById {

        @Test
        @DisplayName("Deux enseignants avec le même ID doivent être égaux")
        void shouldBeEqualWhenSameId() {
            Teacher t1 = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
            Teacher t2 = Teacher.builder().id(1L).firstName("Jane").lastName("Smith").build();

            assertThat(t1).isEqualTo(t2);
            assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }

        @Test
        @DisplayName("Deux enseignants avec des IDs différents ne doivent pas être égaux")
        void shouldNotBeEqualWhenDifferentId() {
            Teacher t1 = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
            Teacher t2 = Teacher.builder().id(2L).firstName("John").lastName("Doe").build();

            assertThat(t1).isNotEqualTo(t2);
        }
    }

    @Nested
    @DisplayName("Fluent setters (@Accessors(chain = true))")
    class FluentSetters {

        @Test
        @DisplayName("Doit supporter le chaînage de setters")
        void shouldSupportMethodChaining() {
            Teacher teacher = new Teacher()
                    .setId(1L)
                    .setFirstName("Marie")
                    .setLastName("Curie");

            assertThat(teacher.getId()).isEqualTo(1L);
            assertThat(teacher.getFirstName()).isEqualTo("Marie");
            assertThat(teacher.getLastName()).isEqualTo("Curie");
        }
    }
}
