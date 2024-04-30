package com.demo.service;

import com.demo.model.Pedido;
import com.demo.model.Produto;
import com.demo.model.dto.PedidoDTO;
import com.demo.repository.PedidoRepository;
import com.demo.repository.ProdutoRepository;
import com.demo.validator.strategy.IPedidoStrategyValidator;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private List<IPedidoStrategyValidator> validationStrategies;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void salvarPedidosMelhorado_Successful() {
        // Setup
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNumeroControle(123L);
        pedidoDTO.setNome("Produto Teste");
        pedidoDTO.setDataCadastro(LocalDate.now());
        pedidoDTO.setQuantidade(10);
        pedidoDTO.setCodigoCliente(1L);

        Produto produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setValor(new BigDecimal("100.00"));

        when(produtoRepository.findByNome("Produto Teste")).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // Execute
        List<PedidoDTO> result = pedidoService.salvarPedidosMelhorado(Lists.list(pedidoDTO));

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("900.00"), result.get(0).getValor());

        // Verifica se o desconto foi aplicado corretamente
        assertEquals(10, result.get(0).getQuantidade());
    }

    @Test
    void salvarPedidosMelhorado_ProductNotFound_ThrowsException() {
        // Setup
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNumeroControle(123L);
        pedidoDTO.setNome("Produto Inexistente");

        when(produtoRepository.findByNome("Produto Inexistente")).thenReturn(Optional.empty());

        // Execute and Verify
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.salvarPedidosMelhorado(Lists.list(pedidoDTO));
        });

        assertEquals("Produto não encontrado: Produto Inexistente", exception.getMessage());
    }
}
