package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Exceptions.RecurosNaoEncontrado;
import DigiStart_Conteudo.Exceptions.ValidacaoException;
import DigiStart_Conteudo.Model.Modulo;
import DigiStart_Conteudo.Repository.ModuloRepository;
import DigiStart_Conteudo.DTO.Input.ModuloRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ModuloService {

    private static final Logger log = LoggerFactory.getLogger(ModuloService.class);
    
    private final ModuloRepository moduloRepository;

    @Autowired(required = false)
    private RabbitMQService rabbitMQService;

    @Autowired
    public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    private void validarNomeModulo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("O nome do módulo não pode ser vazio");
        }
        if (nome.length() < 5) {
            throw new ValidacaoException("O nome do módulo deve ter no mínimo 5 caracteres");
        }
        if (nome.length() > 50) {
            throw new ValidacaoException("O nome do módulo não pode exceder 50 caracteres");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 255) {
            throw new ValidacaoException("A descrição não pode exceder 255 caracteres");
        }
    }

    private void validarProfessorId(Long professorId) {
        if (professorId == null) {
            throw new ValidacaoException("O ID do professor é obrigatório");
        }
        
        if (rabbitMQService != null) {
            rabbitMQService.sendProfessorValidation(professorId);
        }
        
        log.info("Requisição de validação de professor enviada: professorId={}", professorId);
    }

    @Transactional
    public Modulo salvar(ModuloRequestDTO input, Long professorId) {
        validarNomeModulo(input.getNome());
        validarDescricao(input.getDescricao());
        validarProfessorId(professorId);

        Modulo novoModulo = new Modulo();
        novoModulo.setNome(input.getNome());
        novoModulo.setDescricao(input.getDescricao());
        novoModulo.setProfessorId(professorId);
        novoModulo.setAtivo(true);

        Modulo moduloSalvo = moduloRepository.save(novoModulo);
        
        if (rabbitMQService != null) {
            rabbitMQService.sendContentEvent(professorId, "CREATED", "MODULO", moduloSalvo.getId());
        }
        
        return moduloSalvo;
    }

    public List<Modulo> listarTodosAtivos() {
        return moduloRepository.findByAtivoTrue();
    }

    public List<Modulo> listarPorProfessor(Long professorId) {
        return moduloRepository.findByProfessorId(professorId);
    }

    public Modulo buscarPorId(Long id) {
        return moduloRepository.findById(id)
                .orElseThrow(() -> new RecurosNaoEncontrado("Módulo não encontrado com ID: " + id));
    }

    @Transactional
    public Modulo atualizar(Long id, ModuloRequestDTO input) {
        Modulo moduloExistente = buscarPorId(id);

        if (input.getNome() != null && !input.getNome().trim().isEmpty()) {
            validarNomeModulo(input.getNome());
            moduloExistente.setNome(input.getNome());
        }

        if (input.getDescricao() != null) {
            validarDescricao(input.getDescricao());
            moduloExistente.setDescricao(input.getDescricao());
        }

        return moduloRepository.save(moduloExistente);
    }

    @Transactional
    public Boolean desativarModulo(Long moduloId, Long professorId, boolean isBatchOperation) {
        Modulo modulo = moduloRepository.findById(moduloId)
                .orElseThrow(() -> new RecurosNaoEncontrado("Módulo não encontrado"));

        if (!modulo.getProfessorId().equals(professorId)) {
            throw new ValidacaoException("Você não tem permissão para desativar um módulo que não é seu.");
        }

        modulo.setAtivo(false);
        moduloRepository.save(modulo);
        
        if (rabbitMQService != null) {
            rabbitMQService.sendContentEvent(professorId, "DEACTIVATED", "MODULO", moduloId);
        }
        
        if (!isBatchOperation) {
            log.info("Módulo {} desativado com sucesso", moduloId);
        }
        
        return true;
    }

    @Transactional
    public void desativarModulosPorProfessor(Long professorId) {
        List<Modulo> modulos = moduloRepository.findByProfessorId(professorId);
        int desativados = 0;
        
        for (Modulo modulo : modulos) {
            if (modulo.getAtivo()) {
                desativarModulo(modulo.getId(), professorId, true);
                desativados++;
            }
        }
        
        log.info("Desativados {} módulos do professor {}", desativados, professorId);
    }
}
