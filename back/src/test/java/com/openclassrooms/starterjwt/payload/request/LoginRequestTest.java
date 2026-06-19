package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginRequest Tests")
class LoginRequestTest {

    @Test
    @DisplayName("Should create LoginRequest and set properties")
    void testLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        assertThat(loginRequest.getEmail()).isEqualTo("test@example.com");
        assertThat(loginRequest.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        LoginRequest request1 = new LoginRequest();
        request1.setEmail("test@example.com");
        request1.setPassword("password");

        LoginRequest request2 = new LoginRequest();
        request2.setEmail("test@example.com");
        request2.setPassword("password");

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Should generate toString correctly")
    void testToString() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        String toString = loginRequest.toString();
        assertThat(toString).contains("test@example.com");
    }
}

