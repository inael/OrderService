package com.demo.validator.strategy;

import com.demo.model.Pedido;
import com.demo.model.dto.PedidoDTO;
import com.demo.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NumeroControleValidationTest {

    @Mock
    private PedidoRepository pedidoRepository;

    private NumeroControleValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new NumeroControleValidator(pedidoRepository);
    }

    @Test
    void validate_ShouldThrowException_WhenNumeroControleIsAlreadyRegistered() {
        // Setup
        Long numeroControle = 12345L;
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNumeroControle(numeroControle);

        // Simula a existência de um pedido com o mesmo número de controle no banco de dados
        when(pedidoRepository.findByNumeroControle(numeroControle)).thenReturn(Optional.of(new Pedido()));

        // Execute & Verify
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(pedidoDTO);
        });

        assertEquals("Número de controle já cadastrado: " + numeroControle, thrown.getMessage());
        verify(pedidoRepository).findByNumeroControle(numeroControle);
    }

    @Test
    void validate_ShouldNotThrowException_WhenNumeroControleIsNotRegistered() {
        // Setup
        Long numeroControle = 12345L;
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNumeroControle(numeroControle);

        // Simula a não existência de um pedido com o mesmo número de controle no banco de dados
        when(pedidoRepository.findByNumeroControle(numeroControle)).thenReturn(Optional.empty());

        // Execute
        assertDoesNotThrow(() -> validator.validate(pedidoDTO));

        // Verify
        verify(pedidoRepository).findByNumeroControle(numeroControle);
    }
}
