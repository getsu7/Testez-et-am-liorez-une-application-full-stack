package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BadRequestException — Tests unitaires")
class BadRequestExceptionTest {

    @Test
    @DisplayName("Doit être une RuntimeException levable")
    void shouldBeThrowableAsRuntimeException() {
        assertThatThrownBy(() -> { throw new BadRequestException(); })
                .isInstanceOf(BadRequestException.class)
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit porter l'annotation @ResponseStatus(BAD_REQUEST)")
    void shouldCarryBadRequestResponseStatusAnnotation() {
        ResponseStatus annotation = BadRequestException.class.getAnnotation(ResponseStatus.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
