package com.openclassrooms.starterjwt.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour {@link AuthEntryPointJwt}.
 * <p>
 * Note : la méthode {@code commence()} est actuellement commentée dans le code source.
 * Les tests reflètent fidèlement l'implémentation existante.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthEntryPointJwt — Tests unitaires")
class AuthEntryPointJwtTest {

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @DisplayName("Doit être un composant Spring instanciable")
    void shouldBeInstantiableAsSpringComponent() {
        assertThat(authEntryPointJwt).isNotNull();
    }

    @Test
    @DisplayName("Doit porter l'annotation @Component")
    void shouldBeAnnotatedAsComponent() {
        assertThat(AuthEntryPointJwt.class.isAnnotationPresent(Component.class)).isTrue();
    }
}
