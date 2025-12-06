-- Script para corrigir o tamanho das colunas VARCHAR no banco de dados
-- Execute este script no banco de dados 'icompraspedidos'

-- Aumentar o tamanho da coluna chave_pagamento
ALTER TABLE pedido ALTER COLUMN chave_pagamento TYPE VARCHAR(500);

-- Aumentar o tamanho da coluna observacoes
ALTER TABLE pedido ALTER COLUMN observacoes TYPE VARCHAR(1000);

-- Aumentar o tamanho da coluna codigo_rastreio
ALTER TABLE pedido ALTER COLUMN codigo_rastreio TYPE VARCHAR(500);

-- Aumentar o tamanho da coluna url_nf (este é o mais provável causador do erro)
ALTER TABLE pedido ALTER COLUMN url_nf TYPE VARCHAR(1000);

-- Verificar as alterações
SELECT
    column_name,
    character_maximum_length
FROM
    information_schema.columns
WHERE
    table_name = 'pedido'
    AND column_name IN ('chave_pagamento', 'observacoes', 'codigo_rastreio', 'url_nf');

