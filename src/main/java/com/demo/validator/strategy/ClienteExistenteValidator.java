package com.demo.validator.strategy;


import com.demo.model.dto.PedidoDTO;
import com.demo.repository.ClienteRepository;

/**
 * Estratégia de Validação de Cliente Existente
 */
public class ClienteExistenteValidator implements IPedidoStrategyValidator {

    private ClienteRepository clienteRepository;

    public ClienteExistenteValidator(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void validate(PedidoDTO pedidoDTO) {
        clienteRepository.findById(pedidoDTO.getCodigoCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
    }
}
