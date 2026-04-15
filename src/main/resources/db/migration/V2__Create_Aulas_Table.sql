-- Migration para criação da tabela aulas
-- Criada em: 2025-04-15
-- Descrição: Criação da tabela de aulas vinculadas aos módulos

CREATE TABLE IF NOT EXISTS aulas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    video_url VARCHAR(500) NOT NULL,
    ordem INTEGER NOT NULL,
    ativa BOOLEAN DEFAULT true,
    modulo_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_aulas_modulo FOREIGN KEY (modulo_id) REFERENCES modulos(id) ON DELETE CASCADE
);

-- Adiciona índices para performance
CREATE INDEX IF NOT EXISTS idx_aulas_modulo_id ON aulas(modulo_id);
CREATE INDEX IF NOT EXISTS idx_aulas_ativa ON aulas(ativa);
CREATE INDEX IF NOT EXISTS idx_aulas_ordem ON aulas(ordem);
CREATE INDEX IF NOT EXISTS idx_aulas_titulo ON aulas(titulo);

-- Adiciona comentários
COMMENT ON TABLE aulas IS 'Tabela de aulas dos módulos';
COMMENT ON COLUMN aulas.id IS 'Identificador único da aula';
COMMENT ON COLUMN aulas.titulo IS 'Título da aula (10-50 caracteres)';
COMMENT ON COLUMN aulas.descricao IS 'Descrição da aula';
COMMENT ON COLUMN aulas.video_url IS 'URL do vídeo da aula';
COMMENT ON COLUMN aulas.ordem IS 'Ordem da aula dentro do módulo';
COMMENT ON COLUMN aulas.ativa IS 'Status de ativação da aula';
COMMENT ON COLUMN aulas.modulo_id IS 'ID do módulo ao qual a aula pertence';
