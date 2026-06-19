package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NotFoundException — Tests unitaires")
class NotFoundExceptionTest {

    @Test
    @DisplayName("Doit être une RuntimeException levable")
    void shouldBeThrowableAsRuntimeException() {
        assertThatThrownBy(() -> { throw new NotFoundException(); })
                .isInstanceOf(NotFoundException.class)
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit porter l'annotation @ResponseStatus(NOT_FOUND)")
    void shouldCarryNotFoundResponseStatusAnnotation() {
        ResponseStatus annotation = NotFoundException.class.getAnnotation(ResponseStatus.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
