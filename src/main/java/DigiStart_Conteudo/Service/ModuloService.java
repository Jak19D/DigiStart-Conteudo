package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Exceptions.RecurosNaoEncontrado;
import DigiStart_Conteudo.Exceptions.ValidacaoException;
import DigiStart_Conteudo.Model.Modulo;
import DigiStart_Conteudo.Repository.ModuloRepository;
import DigiStart_Conteudo.DTO.Input.ModuloRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ModuloService {

    private final ModuloRepository moduloRepository;

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

    @Transactional
    public Modulo salvar(ModuloRequestDTO input, Long professorId) {
        validarNomeModulo(input.getNome());
        validarDescricao(input.getDescricao());

        Modulo novoModulo = new Modulo();
        novoModulo.setNome(input.getNome());
        novoModulo.setDescricao(input.getDescricao());
        novoModulo.setProfessorId(professorId);
        novoModulo.setAtivo(true);

        return moduloRepository.save(novoModulo);
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
    public Boolean desativarModuloSeDono(Long moduloId, Long professorId) {
        Modulo modulo = moduloRepository.findById(moduloId)
                .orElseThrow(() -> new RecurosNaoEncontrado("Módulo não encontrado"));

        if (!modulo.getProfessorId().equals(professorId)) {
            throw new ValidacaoException("Você não tem permissão para desativar um módulo que não é seu.");
        }

        modulo.setAtivo(false);
        moduloRepository.save(modulo);
        return true;
    }
}
