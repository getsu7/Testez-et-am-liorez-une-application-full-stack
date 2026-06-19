package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires du modèle Session.
 * On teste uniquement le comportement métier (égalité par ID, Accessors chain).
 * Les getter/setter générés par Lombok ne sont pas testés.
 */
@DisplayName("Session — Tests unitaires du modèle")
class SessionTest {

    @Nested
    @DisplayName("equals() et hashCode() — basés sur l'ID")
    class EqualityById {

        @Test
        @DisplayName("Deux sessions avec le même ID doivent être égales")
        void shouldBeEqualWhenSameId() {
            Session s1 = Session.builder().id(1L).name("Session A").build();
            Session s2 = Session.builder().id(1L).name("Session B").build();

            assertThat(s1).isEqualTo(s2);
            assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
        }

        @Test
        @DisplayName("Deux sessions avec des IDs différents ne doivent pas être égales")
        void shouldNotBeEqualWhenDifferentId() {
            Session s1 = Session.builder().id(1L).name("Session A").build();
            Session s2 = Session.builder().id(2L).name("Session A").build();

            assertThat(s1).isNotEqualTo(s2);
        }
    }

    @Nested
    @DisplayName("Fluent setters (@Accessors(chain = true))")
    class FluentSetters {

        @Test
        @DisplayName("Doit supporter le chaînage de setters")
        void shouldSupportMethodChaining() {
            Session session = new Session()
                    .setId(1L)
                    .setName("Yoga Flow")
                    .setDescription("Morning yoga");

            assertThat(session.getId()).isEqualTo(1L);
            assertThat(session.getName()).isEqualTo("Yoga Flow");
            assertThat(session.getDescription()).isEqualTo("Morning yoga");
        }
    }

    @Nested
    @DisplayName("Gestion de la liste des utilisateurs")
    class UsersList {

        @Test
        @DisplayName("Doit permettre l'ajout d'un utilisateur à la session")
        void shouldAllowAddingUserToSession() {
            User user = User.builder().id(1L).email("u@test.com").firstName("A")
                    .lastName("B").password("p").admin(false).build();
            Session session = Session.builder()
                    .id(1L).name("Test").date(new Date())
                    .description("desc").users(new ArrayList<>()).build();

            session.getUsers().add(user);

            assertThat(session.getUsers()).hasSize(1).contains(user);
        }
    }
}
