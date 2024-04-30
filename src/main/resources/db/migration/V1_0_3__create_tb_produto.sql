CREATE TABLE tb_produto (
                            id SERIAL PRIMARY KEY,
                            nome VARCHAR(255) NOT NULL,
                            valor NUMERIC(10, 2) NOT NULL
);

-- Adicionar um índice à coluna 'nome' se houver necessidade de otimização de busca por nome
CREATE INDEX idx_nome_produto ON tb_produto(nome);
