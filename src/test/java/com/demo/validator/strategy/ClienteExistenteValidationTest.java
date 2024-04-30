package com.demo.validator.strategy;


import com.demo.model.Cliente;
import com.demo.model.dto.PedidoDTO;
import com.demo.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ClienteExistenteValidationTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteExistenteValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new ClienteExistenteValidator(clienteRepository);
    }

    @Test
    void validate_ShouldThrowException_WhenClienteDoesNotExist() {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setCodigoCliente(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(pedidoDTO);
        });

        assertEquals("Cliente não encontrado.", exception.getMessage());
        verify(clienteRepository).findById(1L);
    }

    @Test
    void validate_ShouldNotThrowException_WhenClienteExists() {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setCodigoCliente(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente())); // Cliente é a entidade esperada pelo repositório

        assertDoesNotThrow(() -> {
            validator.validate(pedidoDTO);
        });

        verify(clienteRepository).findById(1L);
    }
}
