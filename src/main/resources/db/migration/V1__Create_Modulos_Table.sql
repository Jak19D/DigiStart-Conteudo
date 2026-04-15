-- Migration para criação da tabela modulos
-- Criada em: 2025-04-15
-- Descrição: Criação da tabela principal de módulos do sistema

CREATE TABLE IF NOT EXISTS modulos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    ativo BOOLEAN DEFAULT true,
    professor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Adiciona índices para performance
CREATE INDEX IF NOT EXISTS idx_modulos_professor_id ON modulos(professor_id);
CREATE INDEX IF NOT EXISTS idx_modulos_ativo ON modulos(ativo);
CREATE INDEX IF NOT EXISTS idx_modulos_nome ON modulos(nome);

-- Adiciona comentários
COMMENT ON TABLE modulos IS 'Tabela de módulos de conteúdo do curso';
COMMENT ON COLUMN modulos.id IS 'Identificador único do módulo';
COMMENT ON COLUMN modulos.nome IS 'Nome do módulo (5-50 caracteres)';
COMMENT ON COLUMN modulos.descricao IS 'Descrição detalhada do módulo';
COMMENT ON COLUMN modulos.ativo IS 'Status de ativação do módulo';
COMMENT ON COLUMN modulos.professor_id IS 'ID do professor responsável pelo módulo';
