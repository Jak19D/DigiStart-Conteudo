package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Exceptions.RecurosNaoEncontrado;
import DigiStart_Conteudo.Model.Aula;
import DigiStart_Conteudo.Model.ProgressoAula;
import DigiStart_Conteudo.Model.ProgressoAula.StatusAula;
import DigiStart_Conteudo.Repository.ProgressoAulaRepository;
import DigiStart_Conteudo.Repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressoAulaService {

    private final ProgressoAulaRepository progressoAulaRepository;
    private final AulaRepository aulaRepository;

    @Autowired
    public ProgressoAulaService(ProgressoAulaRepository progressoAulaRepository,
                                AulaRepository aulaRepository) {
        this.progressoAulaRepository = progressoAulaRepository;
        this.aulaRepository = aulaRepository;
    }

    @Transactional
    public ProgressoAula registrarInicio(Long alunoId, Long aulaId) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RecurosNaoEncontrado("Aula não encontrada."));

        Optional<ProgressoAula> progressoOpt = progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaId);

        ProgressoAula progresso;
        if (progressoOpt.isPresent()) {
            progresso = progressoOpt.get();
        } else {
            progresso = new ProgressoAula();
            progresso.setAlunoId(alunoId);
            progresso.setAula(aula);
        }

        if (progresso.getStatus() == StatusAula.PENDENTE) {
            progresso.setStatus(StatusAula.EM_ANDAMENTO);
        }

        return progressoAulaRepository.save(progresso);
    }

    @Transactional
    public ProgressoAula marcarConcluida(Long alunoId, Long aulaId) {
        ProgressoAula progresso = progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaId)
                .orElseThrow(() -> new RecurosNaoEncontrado("Progresso não encontrado."));

        if (progresso.getStatus() != StatusAula.CONCLUIDA) {
            progresso.setStatus(StatusAula.CONCLUIDA);
            progresso.setDataConclusao(LocalDateTime.now());
            return progressoAulaRepository.save(progresso);
        }
        return progresso;
    }

    public boolean verificarPreRequisito(Long alunoId, Long aulaIdAtual) {
        Aula aulaAtual = aulaRepository.findById(aulaIdAtual)
                .orElseThrow(() -> new RecurosNaoEncontrado("Aula não encontrada."));

        List<Aula> aulasDoModulo = aulaRepository.findByModuloIdOrderByOrdemAsc(aulaAtual.getModulo().getId());

        int indiceAulaAtual = aulasDoModulo.indexOf(aulaAtual);

        if (indiceAulaAtual <= 0) {
            return true;
        }

        for (int i = 0; i < indiceAulaAtual; i++) {
            Aula aulaAnterior = aulasDoModulo.get(i);

            Optional<ProgressoAula> progressoAnteriorOpt = progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaAnterior.getId());

            if (progressoAnteriorOpt.isEmpty() || progressoAnteriorOpt.get().getStatus() != StatusAula.CONCLUIDA) {
                return false;
            }
        }

        return true;
    }

    public ProgressoAula buscarProgresso(Long alunoId, Long aulaId) {
        return progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaId)
                .orElse(null);
    }
}
