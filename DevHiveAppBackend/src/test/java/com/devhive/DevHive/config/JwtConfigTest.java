package com.devhive.DevHive.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtConfigTest {

    @Test
    void acceptsSigningKeyWithAtLeastThirtyTwoCharacters() {
        JwtConfig config = new JwtConfig();
        ReflectionTestUtils.setField(config, "secret", "a".repeat(32));

        assertDoesNotThrow(config::validateSecret);
    }

    @Test
    void rejectsSigningKeyShorterThanThirtyTwoCharacters() {
        JwtConfig config = new JwtConfig();
        ReflectionTestUtils.setField(config, "secret", "too-short");

        assertThrows(IllegalStateException.class, config::validateSecret);
    }
}
