package com.demo.validator.chain;


import com.demo.model.dto.PedidoDTO;

import java.util.List;

/**
 * Para lidar com regras de validação de forma mais eficiente
 * e flexível em seu PedidoController,
 * um bom padrão de projeto a ser aplicado é
 * o Chain of Responsibility.
 * Esse padrão permite que você crie uma cadeia
 * de objetos responsáveis por  processar um pedido.
 * Cada objeto na cadeia verifica
 * uma parte específica do pedido e,
 * se possível, processa ou passa para
 * o próximo manipulador na cadeia.
 * Isso torna o sistema mais flexível e a
 * adição de novas regras de validação mais simples.
 */

public interface IPedidoChainValidator {
    void setNextValidator(IPedidoChainValidator nextValidator);
    void validate(List<PedidoDTO> pedidos);
}
