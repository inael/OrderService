CREATE TABLE tb_pedido (
                           id SERIAL PRIMARY KEY,
                           numero_controle BIGINT NOT NULL UNIQUE,
                           data_cadastro DATE,
                           quantidade INTEGER,
                           valor_total NUMERIC(10, 2),
                           cliente_id BIGINT NOT NULL,
                           produto_id BIGINT NOT NULL,
                           CONSTRAINT fk_cliente
                               FOREIGN KEY(cliente_id)
                                   REFERENCES tb_cliente(id)
                                   ON DELETE CASCADE,
                           CONSTRAINT fk_produto
                               FOREIGN KEY(produto_id)
                                   REFERENCES tb_produto(id)
);

-- Caso seja necessário adicionar um índice para a coluna numero_controle para melhorar a performance das consultas
CREATE INDEX idx_numero_controle ON tb_pedido(numero_controle);
