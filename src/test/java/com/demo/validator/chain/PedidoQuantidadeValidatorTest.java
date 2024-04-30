package com.demo.validator.chain;

import com.demo.model.dto.PedidoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoQuantidadeValidatorTest {

    private QuantidadeChainValidator validator;

    @Mock
    private QuantidadeChainValidator nextValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new QuantidadeChainValidator();
        validator.setNextValidator(nextValidator);
    }

    @Test
    void validate_ShouldThrowException_WhenPedidoListExceedsLimit() {
        List<PedidoDTO> pedidos = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            pedidos.add(new PedidoDTO());
        }

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(pedidos),
                "Expected validate() to throw, but it did not"
        );

        assertTrue(thrown.getMessage().contains("Não é permitido enviar mais de 10 pedidos por requisição."));
    }

    @Test
    void validate_ShouldNotThrowException_WhenPedidoListIsUnderLimit() {
        List<PedidoDTO> pedidos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pedidos.add(new PedidoDTO());
        }

        assertDoesNotThrow(() -> validator.validate(pedidos));
        verify(nextValidator, times(1)).validate(pedidos);
    }

    @Test
    void validate_ShouldNotThrowException_WhenPedidoListIsEmpty() {
        assertDoesNotThrow(() -> validator.validate(Collections.emptyList()));
        verify(nextValidator, times(1)).validate(Collections.emptyList());
    }
}
