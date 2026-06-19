package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires du modèle User.
 * On teste uniquement le comportement métier (égalité par ID, Accessors chain, admin flag).
 */
@DisplayName("User — Tests unitaires du modèle")
class UserTest {

    @Nested
    @DisplayName("equals() et hashCode() — basés sur l'ID")
    class EqualityById {

        @Test
        @DisplayName("Deux utilisateurs avec le même ID doivent être égaux")
        void shouldBeEqualWhenSameId() {
            User u1 = User.builder().id(1L).email("a@test.com").firstName("A")
                    .lastName("A").password("p").admin(false).build();
            User u2 = User.builder().id(1L).email("b@test.com").firstName("B")
                    .lastName("B").password("p").admin(true).build();

            assertThat(u1).isEqualTo(u2);
            assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
        }

        @Test
        @DisplayName("Deux utilisateurs avec des IDs différents ne doivent pas être égaux")
        void shouldNotBeEqualWhenDifferentId() {
            User u1 = User.builder().id(1L).email("a@test.com").firstName("A")
                    .lastName("A").password("p").admin(false).build();
            User u2 = User.builder().id(2L).email("a@test.com").firstName("A")
                    .lastName("A").password("p").admin(false).build();

            assertThat(u1).isNotEqualTo(u2);
        }
    }

    @Nested
    @DisplayName("Flag admin")
    class AdminFlag {

        @Test
        @DisplayName("Un utilisateur standard ne doit pas être admin")
        void regularUserShouldNotBeAdmin() {
            User user = User.builder().id(1L).email("u@test.com").firstName("A")
                    .lastName("B").password("p").admin(false).build();
            assertThat(user.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("Un administrateur doit avoir le flag admin à true")
        void adminUserShouldHaveAdminFlag() {
            User admin = User.builder().id(2L).email("admin@test.com").firstName("A")
                    .lastName("B").password("p").admin(true).build();
            assertThat(admin.isAdmin()).isTrue();
        }
    }

    @Nested
    @DisplayName("Fluent setters (@Accessors(chain = true))")
    class FluentSetters {

        @Test
        @DisplayName("Doit supporter le chaînage de setters")
        void shouldSupportMethodChaining() {
            User user = new User()
                    .setId(1L)
                    .setEmail("chain@test.com")
                    .setFirstName("Chain")
                    .setLastName("User")
                    .setPassword("pwd")
                    .setAdmin(false);

            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getEmail()).isEqualTo("chain@test.com");
        }
    }
}
