package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Exceptions.RecurosNaoEncontrado;
import DigiStart_Conteudo.Exceptions.RegraNegocioException;
import DigiStart_Conteudo.Exceptions.ValidacaoException;
import DigiStart_Conteudo.Model.Aula;
import DigiStart_Conteudo.Model.Exercicio;
import DigiStart_Conteudo.Repository.AulaRepository;
import DigiStart_Conteudo.Repository.ExercicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class ExercicioService {

    private static final Logger log = LoggerFactory.getLogger(ExercicioService.class);
    
    private final ExercicioRepository exercicioRepository;
    private final AulaRepository aulaRepository;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    public ExercicioService(ExercicioRepository exercicioRepository, AulaRepository aulaRepository, RabbitMQService rabbitMQService) {
        this.exercicioRepository = exercicioRepository;
        this.aulaRepository = aulaRepository;
        this.rabbitMQService = rabbitMQService;
    }


    public List<Exercicio> listarTodos() {
        return exercicioRepository.findAll();
    }

    public Exercicio buscarPorId(Long id) {
        return exercicioRepository.findById(id)
                .orElseThrow(() -> new RecurosNaoEncontrado("Exercício não encontrado"));
    }

    public List<Exercicio> listarPorAula(Long aulaId) {
        return new ArrayList<>(exercicioRepository.findByAulaId(aulaId));
    }

    @Transactional
    public Exercicio criarNovoExercicio(Long aulaId, String titulo, String descricao, Long professorId) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RecurosNaoEncontrado("Aula não encontrada"));

        if (!aula.getModulo().getProfessorId().equals(professorId)) {
            throw new RegraNegocioException("Você não tem permissão para criar exercícios nesta aula.");
        }

        if (titulo == null || titulo.isEmpty() || descricao == null || descricao.isEmpty()) {
            throw new ValidacaoException("Título e descrição do exercício são obrigatórios.");
        }

        Exercicio novoExercicio = new Exercicio(titulo, descricao, aula);
        Exercicio exercicioSalvo = exercicioRepository.save(novoExercicio);
        
        rabbitMQService.sendContentEvent(professorId, "CREATED", "EXERCICIO", exercicioSalvo.getId());
        
        return exercicioSalvo;
    }

    @Transactional
    public Exercicio atualizar(Long exercicioId, String novoTitulo, String novaDescricao, Long professorId) {
        Exercicio exercicio = exercicioRepository.findById(exercicioId)
                .orElseThrow(() -> new RecurosNaoEncontrado("Exercício não encontrado."));

        if (!exercicio.getAula().getModulo().getProfessorId().equals(professorId)) {
            throw new RegraNegocioException("Você não tem permissão para editar este exercício.");
        }

        if (novoTitulo != null && !novoTitulo.isEmpty()) {
            exercicio.setTitulo(novoTitulo);
        }
        if (novaDescricao != null && !novaDescricao.isEmpty()) {
            exercicio.setDescricao(novaDescricao);
        }

        return exercicioRepository.save(exercicio);
    }

    @Transactional
    public void deletar(Long exercicioId, Long professorId) {
        deletarExercicio(exercicioId, professorId, false);
    }

    @Transactional
    public void deletarExercicio(Long exercicioId, Long professorId, boolean isBatchOperation) {
        Exercicio exercicio = exercicioRepository.findById(exercicioId)
                .orElseThrow(() -> new RecurosNaoEncontrado("Exercício não encontrado."));

        if (!exercicio.getAula().getModulo().getProfessorId().equals(professorId)) {
            throw new RegraNegocioException("Você não tem permissão para deletar este exercício.");
        }

        exercicioRepository.delete(exercicio);
        rabbitMQService.sendContentEvent(professorId, "DELETED", "EXERCICIO", exercicioId);
        
        if (!isBatchOperation) {
            log.info("Exercício {} deletado com sucesso", exercicioId);
        }
    }

    @Transactional
    public void desativarExerciciosPorProfessor(Long professorId) {
        List<Exercicio> exercicios = exercicioRepository.findAllByAulaModuloProfessorId(professorId);
        int removidos = 0;
        
        for (Exercicio exercicio : exercicios) {
            deletarExercicio(exercicio.getId(), professorId, true);
            removidos++;
        }
        
        log.info("Removidos {} exercícios do professor {}", removidos, professorId);
    }

}
