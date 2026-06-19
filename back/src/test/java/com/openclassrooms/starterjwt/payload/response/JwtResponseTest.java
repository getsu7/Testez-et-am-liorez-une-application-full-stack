package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtResponse Tests")
class JwtResponseTest {

    @Test
    @DisplayName("Should create JwtResponse with constructor")
    void testJwtResponseConstructor() {
        JwtResponse jwtResponse = new JwtResponse(
                "jwt-token",
                1L,
                "test@example.com",
                "John",
                "Doe",
                false
        );

        assertThat(jwtResponse.getToken()).isEqualTo("jwt-token");
        assertThat(jwtResponse.getType()).isEqualTo("Bearer");
        assertThat(jwtResponse.getId()).isEqualTo(1L);
        assertThat(jwtResponse.getUsername()).isEqualTo("test@example.com");
        assertThat(jwtResponse.getFirstName()).isEqualTo("John");
        assertThat(jwtResponse.getLastName()).isEqualTo("Doe");
        assertThat(jwtResponse.getAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should create JwtResponse for admin user")
    void testJwtResponseAdmin() {
        JwtResponse jwtResponse = new JwtResponse(
                "admin-jwt-token",
                2L,
                "admin@example.com",
                "Admin",
                "User",
                true
        );

        assertThat(jwtResponse.getAdmin()).isTrue();
        assertThat(jwtResponse.getUsername()).isEqualTo("admin@example.com");
    }

    @Test
    @DisplayName("Should set and get all JwtResponse properties")
    void testJwtResponseGettersAndSetters() {
        JwtResponse jwtResponse = new JwtResponse(
                "token",
                1L,
                "user@test.com",
                "First",
                "Last",
                false
        );

        jwtResponse.setToken("new-token");
        jwtResponse.setType("Custom");
        jwtResponse.setId(2L);
        jwtResponse.setUsername("new@test.com");
        jwtResponse.setFirstName("NewFirst");
        jwtResponse.setLastName("NewLast");
        jwtResponse.setAdmin(true);

        assertThat(jwtResponse.getToken()).isEqualTo("new-token");
        assertThat(jwtResponse.getType()).isEqualTo("Custom");
        assertThat(jwtResponse.getId()).isEqualTo(2L);
        assertThat(jwtResponse.getUsername()).isEqualTo("new@test.com");
        assertThat(jwtResponse.getFirstName()).isEqualTo("NewFirst");
        assertThat(jwtResponse.getLastName()).isEqualTo("NewLast");
        assertThat(jwtResponse.getAdmin()).isTrue();
    }

    @Test
    @DisplayName("Should have default Bearer type")
    void testDefaultBearerType() {
        JwtResponse jwtResponse = new JwtResponse(
                "token",
                1L,
                "user@test.com",
                "First",
                "Last",
                false
        );

        assertThat(jwtResponse.getType()).isEqualTo("Bearer");
    }
}

