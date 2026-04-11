package DigiStart_Conteudo.Controller;

import DigiStart_Conteudo.Service.ModuloService;
import DigiStart_Conteudo.DTO.Output.ModuloResponseDTO;
import DigiStart_Conteudo.DTO.Input.ModuloRequestDTO;
import DigiStart_Conteudo.Mapper.ModuloMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
public class ModuloRestController {

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private ModuloMapper moduloMapper;

    @GetMapping
    public List<ModuloResponseDTO> listarTodos() {
        return moduloService.listarTodosAtivos().stream()
                .map(moduloMapper::toResponseDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(moduloMapper.toResponseDTO(moduloService.buscarPorId(id)));
    }

    @PostMapping("/professor/{professorId}")
    public ResponseEntity<ModuloResponseDTO> criar(@RequestBody @Valid ModuloRequestDTO input, @PathVariable Long professorId) {
        var modulo = moduloService.salvar(input, professorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(moduloMapper.toResponseDTO(modulo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ModuloRequestDTO input) {
        var modulo = moduloService.atualizar(id, input);
        return ResponseEntity.ok(moduloMapper.toResponseDTO(modulo));
    }

    @GetMapping("/professor/{professorId}")
    public List<ModuloResponseDTO> listarPorProfessor(@PathVariable Long professorId) {
        return moduloService.listarPorProfessor(professorId).stream()
                .map(moduloMapper::toResponseDTO).toList();
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id, @RequestParam Long professorId) {
        moduloService.desativarModulo(id, professorId, false);
    }
}
