

package com.demo.service;

import com.demo.exception.ClienteNotFoundException;
import com.demo.model.Cliente;
import com.demo.model.Produto;
import com.demo.model.dto.PedidoDTO;
import com.demo.repository.PedidoRepository;
import com.demo.model.Pedido;
import com.demo.repository.ClienteRepository;
import com.demo.repository.ProdutoRepository;
import com.demo.validator.strategy.IPedidoStrategyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    // Utilizei logger para rastrear ações como criar o pedido, buscar por Id, etc,
    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    @Autowired
    private PedidoRepository pedidoRepository;


    @Autowired
    private ClienteRepository clienteRepository;


    @Autowired
    private ProdutoRepository produtoRepository;

    private final List<IPedidoStrategyValidator> validationStrategies;

    @Autowired
    public PedidoService(List<IPedidoStrategyValidator> validationStrategies) {
        this.validationStrategies = validationStrategies;
    }

    @Transactional
    public List<PedidoDTO> salvarPedidos(List<PedidoDTO> pedidoDTOs) throws IllegalArgumentException {
        List<PedidoDTO> savedPedidos = new ArrayList<>();
        for (PedidoDTO pedidoDTO : pedidoDTOs) {
            // Verificar se o cliente existe
            Cliente cliente = clienteRepository.findById(pedidoDTO.getCodigoCliente())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com o código: " + pedidoDTO.getCodigoCliente()));


            // Verifica se o número de controle já existe
            pedidoRepository.findByNumeroControle(pedidoDTO.getNumeroControle()).ifPresent(p -> {
                throw new IllegalArgumentException("Número de controle já cadastrado: " + pedidoDTO.getNumeroControle());
            });

            if (pedidoRepository.findByNumeroControle(pedidoDTO.getNumeroControle()).isPresent()) {
                throw new IllegalArgumentException("Número de controle já cadastrado: " + pedidoDTO.getNumeroControle());
            }
            // Busca o produto pelo nome
            Produto produto = produtoRepository.findByNome(pedidoDTO.getNome())
                    .orElseGet(() -> {
                        Produto novoProduto = new Produto();
                        novoProduto.setNome(pedidoDTO.getNome());
                        novoProduto.setValor(pedidoDTO.getValor());
                        produtoRepository.save(novoProduto);
                        return novoProduto;
                    });

            Pedido pedido = new Pedido();
            pedido.setNumeroControle(pedidoDTO.getNumeroControle());
            //Caso a data de cadastro não seja enviada o sistema deve assumir a data atual.
            pedido.setDataCadastro(pedidoDTO.getDataCadastro() != null ? pedidoDTO.getDataCadastro() : LocalDate.now());
            pedido.setProduto(produto);
            //Caso a quantidade não seja enviada considerar 1.
            pedido.setQuantidade(pedidoDTO.getQuantidade() != null ? pedidoDTO.getQuantidade() : 1);
            pedido.setCliente(cliente);

            // Aplica desconto baseado na quantidade
            BigDecimal desconto = calculateDiscount(pedido.getQuantidade());
            BigDecimal valorTotal = calculateTotalValue(pedido.getProduto().getValor(), pedido.getQuantidade(), desconto);
            pedido.setValorTotal(valorTotal);

            Pedido savedPedido = pedidoRepository.save(pedido);
            savedPedidos.add(convertToDTO(savedPedido));
        }
        return savedPedidos;
    }

    @Transactional
    public List<PedidoDTO> salvarPedidosMelhorado(List<PedidoDTO> pedidoDTOs) {
        List<PedidoDTO> savedPedidos = new ArrayList<>();
        for (PedidoDTO pedidoDTO : pedidoDTOs) {
            // Aplica cada estratégia de validação
            validationStrategies.forEach(strategy -> strategy.validate(pedidoDTO));

            // Busca o produto pelo nome
            Produto produto = produtoRepository.findByNome(pedidoDTO.getNome())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + pedidoDTO.getNome()));

            Pedido pedido = new Pedido();
            pedido.setNumeroControle(pedidoDTO.getNumeroControle());
            pedido.setDataCadastro(pedidoDTO.getDataCadastro() != null ? pedidoDTO.getDataCadastro() : LocalDate.now());
            pedido.setProduto(produto);
            pedido.setQuantidade(pedidoDTO.getQuantidade() != null ? pedidoDTO.getQuantidade() : 1);
            pedido.setCliente(new Cliente(pedidoDTO.getCodigoCliente()));

            // Aplica desconto e calcula o valor total
            BigDecimal desconto = calculateDiscount(pedido.getQuantidade());
            BigDecimal valorTotal = calculateTotalValue(pedido.getProduto().getValor(), pedido.getQuantidade(), desconto);
            pedido.setValorTotal(valorTotal);

            Pedido savedPedido = pedidoRepository.save(pedido);
            savedPedidos.add(convertToDTO(savedPedido));
        }
        return savedPedidos;
    }

    private BigDecimal calculateDiscount(int quantidade) {
        if (quantidade >= 10) {
            return BigDecimal.valueOf(0.10);
        } else if (quantidade > 5) {
            return BigDecimal.valueOf(0.05);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateTotalValue(BigDecimal unitPrice, int quantity, BigDecimal discountRate) {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return total.subtract(total.multiply(discountRate));
    }

    private PedidoDTO convertToDTO(Pedido pedidoSalvo) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedidoSalvo.getId());
        dto.setNumeroControle(pedidoSalvo.getNumeroControle());
        dto.setDataCadastro(pedidoSalvo.getDataCadastro());
        dto.setNome(pedidoSalvo.getProduto().getNome());
        dto.setValor(pedidoSalvo.getValorTotal());
        dto.setQuantidade(pedidoSalvo.getQuantidade());
        dto.setCodigoCliente(pedidoSalvo.getCliente().getId());
        dto.setValor(pedidoSalvo.getProduto().getValor());
        return dto;
    }

    // Retorno um unico pedido pelo id
    @Transactional(readOnly = true)
    public Optional<PedidoDTO> findById(Long id) {
        return pedidoRepository.findById(id).map(PedidoDTO::new);
    }

    //  Retorna todos os pedidos
    @Transactional(readOnly = true)
    public List<PedidoDTO> findAll() {
        return pedidoRepository.findAll().stream().map(PedidoDTO::new).collect(Collectors.toList());
    }

    // retorna um lista de pedidos por data de cadastro
    @Transactional(readOnly = true)
    public List<PedidoDTO> findByDataCadastro(LocalDate dataCadastro) {
        List<Pedido> result = pedidoRepository.findByDataCadastro(dataCadastro);
        return result.isEmpty() ? Collections.emptyList()
                : result.stream().map(PedidoDTO::new).collect(Collectors.toList());
    }

    // Metodo para deletar pedido por Id
    @Transactional
    public void deletaPedidoPorId(Long id) {
        pedidoRepository.deleteById(id);
    }

    // Metodo para buscar pedido pelo número de controle informado pelo usuário
    @Transactional(readOnly = true)
    public Optional<PedidoDTO> findByNumeroControle(Long numeroControle) {
        return pedidoRepository.findByNumeroControle(numeroControle).map(PedidoDTO::new);
    }
}
