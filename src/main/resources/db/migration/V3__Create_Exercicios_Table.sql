-- Migration para criação da tabela exercicios
-- Criada em: 2025-04-15
-- Descrição: Criação da tabela de exercícios vinculados às aulas

CREATE TABLE IF NOT EXISTS exercicios (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    aula_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_exercicios_aula FOREIGN KEY (aula_id) REFERENCES aulas(id) ON DELETE CASCADE
);

-- Adiciona índices para performance
CREATE INDEX IF NOT EXISTS idx_exercicios_aula_id ON exercicios(aula_id);
CREATE INDEX IF NOT EXISTS idx_exercicios_titulo ON exercicios(titulo);

-- Adiciona comentários
COMMENT ON TABLE exercicios IS 'Tabela de exercícios das aulas';
COMMENT ON COLUMN exercicios.id IS 'Identificador único do exercício';
COMMENT ON COLUMN exercicios.titulo IS 'Título do exercício (até 100 caracteres)';
COMMENT ON COLUMN exercicios.descricao IS 'Descrição detalhada do exercício (até 500 caracteres)';
COMMENT ON COLUMN exercicios.aula_id IS 'ID da aula à qual o exercício pertence';
