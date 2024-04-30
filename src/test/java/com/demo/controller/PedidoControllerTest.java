package com.demo.controller;

import com.demo.model.dto.PedidoDTO;
import com.demo.service.PedidoService;
import com.demo.validator.chain.IPedidoChainValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @MockBean
    private IPedidoChainValidator validatorChain;

    @InjectMocks
    private PedidoController pedidoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void criarPedidos_ShouldReturnSavedPedidos() {
        List<PedidoDTO> pedidos = Arrays.asList(new PedidoDTO());
        when(pedidoService.salvarPedidos(pedidos)).thenReturn(pedidos);

        ResponseEntity<Object> response = pedidoController.criarPedidos(pedidos);


        verify(pedidoService).salvarPedidos(pedidos);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidos, response.getBody());
    }

    @Test
    void listarTodosPedidos_ShouldReturnAllPedidos() {
        List<PedidoDTO> pedidos = Arrays.asList(new PedidoDTO());
        when(pedidoService.findAll()).thenReturn(pedidos);

        ResponseEntity<List<PedidoDTO>> response = pedidoController.listarTodosPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidos, response.getBody());
    }

    @Test
    void buscarPedidoPorId_NotFound() {
        Long id = 1L;
        when(pedidoService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> response = pedidoController.buscarPedidoPorId(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPedidoPorId_Found() {
        Long id = 1L;
        PedidoDTO pedidoDTO = new PedidoDTO();
        when(pedidoService.findById(id)).thenReturn(Optional.of(pedidoDTO));

        ResponseEntity<?> response = pedidoController.buscarPedidoPorId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoDTO, response.getBody());
    }

    @Test
    void buscarPedidoPorNumeroControle_NotFound() {
        Long numeroControle = 1L;
        when(pedidoService.findByNumeroControle(numeroControle)).thenReturn(Optional.empty());

        ResponseEntity<?> response = pedidoController.buscarPedidoPorNumeroControle(numeroControle);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPedidoPorNumeroControle_Found() {
        Long numeroControle = 1L;
        PedidoDTO pedidoDTO = new PedidoDTO();
        when(pedidoService.findByNumeroControle(numeroControle)).thenReturn(Optional.of(pedidoDTO));

        ResponseEntity<?> response = pedidoController.buscarPedidoPorNumeroControle(numeroControle);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoDTO, response.getBody());
    }

    @Test
    void buscarPedidosPorDataCadastro_NoContent() {
        LocalDate data = LocalDate.now();
        when(pedidoService.findByDataCadastro(data)).thenReturn(Arrays.asList());

        ResponseEntity<List<PedidoDTO>> response = pedidoController.buscarPedidosPorDataCadastro(data);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void buscarPedidosPorDataCadastro_Found() {
        LocalDate data = LocalDate.now();
        List<PedidoDTO> pedidos = Arrays.asList(new PedidoDTO());
        when(pedidoService.findByDataCadastro(data)).thenReturn(pedidos);

        ResponseEntity<List<PedidoDTO>> response = pedidoController.buscarPedidosPorDataCadastro(data);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidos, response.getBody());
    }

    @Test
    void deletarPedido_NotFound() {
        Long id = 1L;
        doThrow(new RuntimeException("Not Found")).when(pedidoService).deletaPedidoPorId(id);

        ResponseEntity<?> response = pedidoController.deletarPedido(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deletarPedido_Success() {
        Long id = 1L;
        doNothing().when(pedidoService).deletaPedidoPorId(id);

        ResponseEntity<?> response = pedidoController.deletarPedido(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedido deletado com sucesso.", response.getBody());
    }
}
