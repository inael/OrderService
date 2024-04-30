package com.demo.validator.strategy;

import com.demo.model.dto.PedidoDTO;

/**
 * O padrão de projeto Strategy permite definir uma família de algoritmos, encapsular cada um como um
 * objeto e torná-los intercambiáveis. O Strategy permite que o algoritmo varie independentemente dos clientes que o
 * utilizam, o que é ideal para este caso, onde diferentes regras de validação podem ser necessárias.
 */
public interface IPedidoStrategyValidator {
    void validate(PedidoDTO pedidoDTO);
}
