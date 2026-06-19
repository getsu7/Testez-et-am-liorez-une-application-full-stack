package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SignupRequest Tests")
class SignupRequestTest {

    @Test
    @DisplayName("Should create SignupRequest and set properties")
    void testSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        assertThat(signupRequest.getEmail()).isEqualTo("test@example.com");
        assertThat(signupRequest.getFirstName()).isEqualTo("John");
        assertThat(signupRequest.getLastName()).isEqualTo("Doe");
        assertThat(signupRequest.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password");

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Should generate toString correctly")
    void testToString() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");

        String toString = signupRequest.toString();
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("John");
        assertThat(toString).contains("Doe");
    }
}

