-- Migration para criação da tabela progresso_aulas
-- Criada em: 2025-04-15
-- Descrição: Criação da tabela de progresso de aulas por aluno

CREATE TABLE IF NOT EXISTS progresso_aulas (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    aula_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    tempo_assistido_segundos BIGINT DEFAULT 0,
    data_conclusao TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_progresso_aulas_aula FOREIGN KEY (aula_id) REFERENCES aulas(id) ON DELETE CASCADE,
    CONSTRAINT chk_status CHECK (status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA')),
    CONSTRAINT chk_tempo CHECK (tempo_assistido_segundos >= 0),
    CONSTRAINT uk_aluno_aula UNIQUE (aluno_id, aula_id)
);

-- Adiciona índices para performance
CREATE INDEX IF NOT EXISTS idx_progresso_aluno_id ON progresso_aulas(aluno_id);
CREATE INDEX IF NOT EXISTS idx_progresso_aula_id ON progresso_aulas(aula_id);
CREATE INDEX IF NOT EXISTS idx_progresso_status ON progresso_aulas(status);
CREATE INDEX IF NOT EXISTS idx_progresso_data_conclusao ON progresso_aulas(data_conclusao);

-- Adiciona comentários
COMMENT ON TABLE progresso_aulas IS 'Tabela de progresso das aulas por aluno';
COMMENT ON COLUMN progresso_aulas.id IS 'Identificador único do registro de progresso';
COMMENT ON COLUMN progresso_aulas.aluno_id IS 'ID do aluno';
COMMENT ON COLUMN progresso_aulas.aula_id IS 'ID da aula';
COMMENT ON COLUMN progresso_aulas.status IS 'Status do progresso (PENDENTE, EM_ANDAMENTO, CONCLUIDA)';
COMMENT ON COLUMN progresso_aulas.tempo_assistido_segundos IS 'Tempo assistido em segundos';
COMMENT ON COLUMN progresso_aulas.data_conclusao IS 'Data de conclusão da aula';
