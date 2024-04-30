package com.demo.validator.strategy;

import com.demo.model.dto.PedidoDTO;
import com.demo.repository.PedidoRepository;

/**
 * Estratégia de Validação de Número de Controle
 */
public class NumeroControleValidator implements IPedidoStrategyValidator {

    private PedidoRepository pedidoRepository;

    public NumeroControleValidator(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void validate(PedidoDTO pedidoDTO) {
        if (pedidoRepository.findByNumeroControle(pedidoDTO.getNumeroControle()).isPresent()) {
            throw new IllegalArgumentException("Número de controle já cadastrado: " + pedidoDTO.getNumeroControle());
        }
    }
}
