package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Exceptions.RegraNegocioException;
import DigiStart_Conteudo.Exceptions.RecurosNaoEncontrado;
import DigiStart_Conteudo.Exceptions.ValidacaoException;
import DigiStart_Conteudo.Model.Aula;
import DigiStart_Conteudo.Model.Exercicio;
import DigiStart_Conteudo.Repository.AulaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AulaService {

    private static final Logger log = LoggerFactory.getLogger(AulaService.class);
    
    private final AulaRepository aulaRepository;
    private final ProgressoAulaService progressoAulaService;
    private final ExercicioService exercicioService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    public AulaService(AulaRepository aulaRepository, ProgressoAulaService progressoAulaService, ExercicioService exercicioService, RabbitMQService rabbitMQService) {
        this.aulaRepository = aulaRepository;
        this.progressoAulaService = progressoAulaService;
        this.exercicioService = exercicioService;
        this.rabbitMQService = rabbitMQService;
    }


    private void validarVideo(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            throw new ValidacaoException("O vídeo da aula é obrigatório.");
        }
        if (!videoUrl.toLowerCase().endsWith(".mp4")) {
            throw new ValidacaoException("Só são aceitos arquivos no formato MP4.");
        }
    }

    @Transactional
    public Aula adicionarAula(Aula novaAula) {
        if (novaAula.getTitulo() == null || novaAula.getTitulo().length() < 10 || novaAula.getTitulo().length() > 50) {
            throw new ValidacaoException("Título da aula deve ter entre 10 e 50 caracteres.");
        }
        validarVideo(novaAula.getVideoUrl());

        int proximaOrdem = aulaRepository.countByModulo(novaAula.getModulo()) + 1;
        novaAula.setOrdem(proximaOrdem);

        novaAula.setAtiva(true);

        return aulaRepository.save(novaAula);
    }

    @Transactional
    public Aula atualizar(Long id, Aula aulaAtualizada, Long professorId) {
        return aulaRepository.findById(id)
                .map(aulaExistente -> {
                    if (!aulaExistente.getModulo().getProfessorId().equals(professorId)) {
                        throw new RegraNegocioException("Você não tem permissão para editar esta aula.");
                    }

                    if (aulaAtualizada.getTitulo() != null) {
                        if (aulaAtualizada.getTitulo().length() < 10 || aulaAtualizada.getTitulo().length() > 50) {
                            throw new ValidacaoException("Novo título da aula deve ter entre 10 e 50 caracteres.");
                        }
                        aulaExistente.setTitulo(aulaAtualizada.getTitulo());
                    }

                    if (aulaAtualizada.getDescricao() != null) {
                        if (aulaAtualizada.getDescricao().length() < 10 || aulaAtualizada.getDescricao().length() > 255) {
                            throw new ValidacaoException("A descrição deve ter entre 10 e 255 caracteres.");
                        }
                        aulaExistente.setDescricao(aulaAtualizada.getDescricao());
                    }

                    if (aulaAtualizada.getVideoUrl() != null) {
                        validarVideo(aulaAtualizada.getVideoUrl());
                        aulaExistente.setVideoUrl(aulaAtualizada.getVideoUrl());
                    }

                    return aulaRepository.save(aulaExistente);
                })
                .orElseThrow(() -> new RecurosNaoEncontrado("Aula não encontrada com o id: " + id));
    }

    @Transactional
    public void deletar(Long id, Long professorId) {
        desativarAula(id, professorId, false);
    }

    @Transactional
    public Boolean desativarAula(Long aulaId, Long professorId, boolean isBatchOperation) {
        Aula aula = buscarPorId(aulaId);

        if (!aula.getModulo().getProfessorId().equals(professorId)) {
            throw new RegraNegocioException("Você não tem permissão para desativar esta aula.");
        }

        aula.setAtiva(false);
        aulaRepository.save(aula);
        
        rabbitMQService.sendContentEvent(professorId, "DEACTIVATED", "AULA", aulaId);
        
        if (!isBatchOperation) {
            log.info("Aula {} desativada com sucesso", aulaId);
        }
        
        return true;
    }


    @Transactional
    public Aula acessarAula(Long alunoId, Long aulaId) {
        Aula aula = buscarPorId(aulaId);

        if (!aula.getModulo().getAtivo() || !aula.isAtiva()) {
            throw new RegraNegocioException("Este conteúdo está indisponível no momento.");
        }

        if (!progressoAulaService.verificarPreRequisito(alunoId, aulaId)) {
            throw new RegraNegocioException("Violação de Regra de Negócio (RN02/08): Aula bloqueada. Conclua as aulas anteriores para liberar o acesso.");
        }

        progressoAulaService.registrarInicio(alunoId, aulaId);

        return aula;
    }

    @Transactional
    public void marcarComoConcluida(Long alunoId, Long aulaId) {
        progressoAulaService.marcarConcluida(alunoId, aulaId);

        List<Exercicio> exercicios = exercicioService.listarPorAula(aulaId);

        if (!exercicios.isEmpty()) {
            System.out.println("LOG: Aula concluída, mas possui exercícios. RN 11 Ativada.");
        }

    }


    public List<Aula> listar() {
        return aulaRepository.findAll();
    }


    public List<Aula> listarPorModulo(Long moduloId) {
        return aulaRepository.findByModuloIdOrderByOrdemAsc(moduloId);
    }

    public Aula buscarPorId(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new RecurosNaoEncontrado("Aula não encontrada com o id: " + id));
    }

    @Transactional
    public void desativarAulasPorProfessor(Long professorId) {
        List<Aula> aulas = aulaRepository.findAllByModuloProfessorId(professorId);
        int desativadas = 0;
        
        for (Aula aula : aulas) {
            if (aula.isAtiva()) {
                desativarAula(aula.getId(), professorId, true);
                desativadas++;
            }
        }
        
        log.info("Desativadas {} aulas do professor {}", desativadas, professorId);
    }
}
