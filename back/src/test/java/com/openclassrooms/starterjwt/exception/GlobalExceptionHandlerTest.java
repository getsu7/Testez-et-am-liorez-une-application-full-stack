package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler — Tests unitaires")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Nested
    @DisplayName("handleNumberFormatException()")
    class HandleNumberFormatException {

        @Test
        @DisplayName("Doit retourner 400 BAD_REQUEST sans corps")
        void shouldReturn400WithNoBody() {
            ResponseEntity<?> response = globalExceptionHandler
                    .handleNumberFormatException(new NumberFormatException("Not a number"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNull();
        }
    }

    @Nested
    @DisplayName("handleNotFoundException()")
    class HandleNotFoundException {

        @Test
        @DisplayName("Doit retourner 404 NOT_FOUND sans corps")
        void shouldReturn404WithNoBody() {
            ResponseEntity<?> response = globalExceptionHandler
                    .handleNotFoundException(new NotFoundException());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
        }
    }

    @Nested
    @DisplayName("handleBadRequestException()")
    class HandleBadRequestException {

        @Test
        @DisplayName("Doit retourner 400 BAD_REQUEST sans corps")
        void shouldReturn400WithNoBody() {
            ResponseEntity<?> response = globalExceptionHandler
                    .handleBadRequestException(new BadRequestException());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNull();
        }
    }
}
