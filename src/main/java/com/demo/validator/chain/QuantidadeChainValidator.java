package com.demo.validator.chain;


import com.demo.model.dto.PedidoDTO;

import java.util.List;

public class QuantidadeChainValidator implements IPedidoChainValidator {
    private IPedidoChainValidator nextValidator;

    @Override
    public void setNextValidator(IPedidoChainValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(List<PedidoDTO> pedidos) {
        if (pedidos.size() > 10) {
            throw new IllegalArgumentException("Não é permitido enviar mais de 10 pedidos por requisição.");
        }
        if (nextValidator != null) {
            nextValidator.validate(pedidos);
        }
    }
}
