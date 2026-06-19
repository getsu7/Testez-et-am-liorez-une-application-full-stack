package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDetailsImpl — Tests unitaires")
class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encoded")
                .admin(true)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Accesseurs
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Accesseurs")
    class Accessors {

        @Test
        @DisplayName("Doit exposer l'id, le username et le mot de passe correctement")
        void shouldExposeIdUsernameAndPassword() {
            assertThat(userDetails.getId()).isEqualTo(1L);
            assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
            assertThat(userDetails.getPassword()).isEqualTo("encoded");
        }

        @Test
        @DisplayName("Doit exposer le flag admin correctement")
        void shouldExposeAdminFlag() {
            assertThat(userDetails.getAdmin()).isTrue();
        }

        @Test
        @DisplayName("Doit retourner une collection d'autorités vide")
        void shouldReturnEmptyAuthorities() {
            assertThat(userDetails.getAuthorities()).isNotNull().isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Statut du compte
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Statut du compte")
    class AccountStatus {

        @Test
        @DisplayName("Doit indiquer que le compte n'est pas expiré")
        void shouldReportAccountNonExpired() {
            assertThat(userDetails.isAccountNonExpired()).isTrue();
        }

        @Test
        @DisplayName("Doit indiquer que le compte n'est pas verrouillé")
        void shouldReportAccountNonLocked() {
            assertThat(userDetails.isAccountNonLocked()).isTrue();
        }

        @Test
        @DisplayName("Doit indiquer que les credentials ne sont pas expirés")
        void shouldReportCredentialsNonExpired() {
            assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        }

        @Test
        @DisplayName("Doit indiquer que le compte est activé")
        void shouldReportAccountEnabled() {
            assertThat(userDetails.isEnabled()).isTrue();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // equals() — basé sur l'id
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("equals()")
    class Equals {

        @Test
        @DisplayName("Doit être égal à lui-même")
        void shouldBeEqualToItself() {
            assertThat(userDetails.equals(userDetails)).isTrue();
        }

        @Test
        @DisplayName("Deux instances avec le même id doivent être égales")
        void shouldBeEqualWhenSameId() {
            UserDetailsImpl other = UserDetailsImpl.builder()
                    .id(1L).username("other@example.com")
                    .firstName("Jane").lastName("Smith")
                    .password("other").admin(false)
                    .build();

            assertThat(userDetails).isEqualTo(other);
        }

        @Test
        @DisplayName("Deux instances avec des ids différents ne doivent pas être égales")
        void shouldNotBeEqualWhenDifferentId() {
            UserDetailsImpl other = UserDetailsImpl.builder()
                    .id(2L).username("test@example.com")
                    .firstName("John").lastName("Doe")
                    .password("encoded").admin(true)
                    .build();

            assertThat(userDetails).isNotEqualTo(other);
        }

        @Test
        @DisplayName("Doit retourner false pour null")
        void shouldNotBeEqualToNull() {
            assertThat(userDetails.equals(null)).isFalse();
        }

        @Test
        @DisplayName("Doit retourner false pour un objet d'un autre type")
        void shouldNotBeEqualToDifferentType() {
            assertThat(userDetails.equals("not-a-UserDetailsImpl")).isFalse();
        }
    }
}
