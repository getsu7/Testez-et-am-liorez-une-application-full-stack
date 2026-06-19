package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils — Tests unitaires")
class JwtUtilsTest {

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtUtils jwtUtils;

    private UserDetailsImpl userDetails;
    private String jwtSecret;
    private static final int JWT_EXPIRATION_MS = 86_400_000; // 24 h

    @BeforeEach
    void setUp() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        jwtSecret = Base64.getEncoder().encodeToString(key.getEncoded());

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", JWT_EXPIRATION_MS);

        userDetails = UserDetailsImpl.builder()
                .id(1L).username("test@example.com")
                .firstName("John").lastName("Doe")
                .password("encoded").admin(false)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // generateJwtToken()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("generateJwtToken()")
    class GenerateJwtToken {

        @Test
        @DisplayName("Doit générer un token JWT non vide")
        void shouldGenerateNonEmptyToken() {
            when(authentication.getPrincipal()).thenReturn(userDetails);

            String token = jwtUtils.generateJwtToken(authentication);

            assertThat(token).isNotNull().isNotEmpty();
            verify(authentication).getPrincipal();
        }

        @Test
        @DisplayName("Doit encoder le nom d'utilisateur dans le subject du token")
        void shouldEncodeUsernameAsSubject() {
            when(authentication.getPrincipal()).thenReturn(userDetails);

            String token = jwtUtils.generateJwtToken(authentication);

            assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("test@example.com");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getUserNameFromJwtToken()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getUserNameFromJwtToken()")
    class GetUserNameFromJwtToken {

        @Test
        @DisplayName("Doit extraire le nom d'utilisateur d'un token valide")
        void shouldExtractUsernameFromValidToken() {
            when(authentication.getPrincipal()).thenReturn(userDetails);
            String token = jwtUtils.generateJwtToken(authentication);

            String username = jwtUtils.getUserNameFromJwtToken(token);

            assertThat(username).isEqualTo("test@example.com");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // validateJwtToken()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("validateJwtToken()")
    class ValidateJwtToken {

        @Test
        @DisplayName("Doit retourner true pour un token valide")
        void shouldReturnTrueForValidToken() {
            when(authentication.getPrincipal()).thenReturn(userDetails);
            String token = jwtUtils.generateJwtToken(authentication);

            assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        }

        @ParameterizedTest(name = "token = \"{0}\"")
        @ValueSource(strings = {"", "invalid", "malformed.jwt.token", "a.b.c"})
        @DisplayName("Doit retourner false pour des tokens invalides ou malformés")
        void shouldReturnFalseForInvalidTokens(String invalidToken) {
            assertThat(jwtUtils.validateJwtToken(invalidToken)).isFalse();
        }

        @Test
        @DisplayName("Doit retourner false pour un token expiré")
        void shouldReturnFalseForExpiredToken() {
            String expiredToken = Jwts.builder()
                    .setSubject("test@example.com")
                    .setIssuedAt(new Date(System.currentTimeMillis() - 100_000))
                    .setExpiration(new Date(System.currentTimeMillis() - 10_000))
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();

            assertThat(jwtUtils.validateJwtToken(expiredToken)).isFalse();
        }

        @Test
        @DisplayName("Doit retourner false pour un token signé avec une clé différente")
        void shouldReturnFalseForTokenWithWrongSignature() {
            SecretKey otherKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            String otherSecret = Base64.getEncoder().encodeToString(otherKey.getEncoded());

            String tokenWithWrongSignature = Jwts.builder()
                    .setSubject("test@example.com")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                    .signWith(SignatureAlgorithm.HS512, otherSecret)
                    .compact();

            assertThat(jwtUtils.validateJwtToken(tokenWithWrongSignature)).isFalse();
        }
    }
}
