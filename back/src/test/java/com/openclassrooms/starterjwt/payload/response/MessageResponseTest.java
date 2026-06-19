package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageResponse Tests")
class MessageResponseTest {

    @Test
    @DisplayName("Should create MessageResponse with constructor")
    void testMessageResponseConstructor() {
        MessageResponse messageResponse = new MessageResponse("Success!");

        assertThat(messageResponse.getMessage()).isEqualTo("Success!");
    }

    @Test
    @DisplayName("Should set and get message")
    void testMessageResponseGetterAndSetter() {
        MessageResponse messageResponse = new MessageResponse("Initial message");

        messageResponse.setMessage("Updated message");

        assertThat(messageResponse.getMessage()).isEqualTo("Updated message");
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        MessageResponse response1 = new MessageResponse("Message");
        MessageResponse response2 = new MessageResponse("Message");

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Should generate toString correctly")
    void testToString() {
        MessageResponse messageResponse = new MessageResponse("Test message");

        String toString = messageResponse.toString();
        assertThat(toString).contains("Test message");
    }

    @Test
    @DisplayName("Should handle error messages")
    void testErrorMessage() {
        MessageResponse messageResponse = new MessageResponse("Error: Email is already taken!");

        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");
    }
}

