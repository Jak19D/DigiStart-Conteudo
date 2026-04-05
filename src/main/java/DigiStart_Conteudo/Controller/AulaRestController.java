package DigiStart_Conteudo.Controller;

import DigiStart_Conteudo.DTO.Input.AulaRequestDTO;
import DigiStart_Conteudo.DTO.Output.AulaResponseDTO;
import DigiStart_Conteudo.Mapper.AulaMapper;
import DigiStart_Conteudo.Model.Aula;
import DigiStart_Conteudo.Service.AulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aulas")
public class AulaRestController {

    @Autowired
    private AulaService aulaService;

    @Autowired
    private AulaMapper aulaMapper;

    @GetMapping("/modulo/{moduloId}")
    public ResponseEntity<List<AulaResponseDTO>> listarPorModulo(@PathVariable Long moduloId) {
        var aulas = aulaService.listarPorModulo(moduloId)
                .stream()
                .map(aulaMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(aulas);
    }

    @GetMapping
    public ResponseEntity<List<AulaResponseDTO>> listar() {
        var aulas = aulaService.listar()
                .stream()
                .map(aulaMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(aulas);
    }

    @PostMapping
    public ResponseEntity<AulaResponseDTO> adicionar(@RequestBody AulaRequestDTO aulaRequestDTO) {
        Aula novaAula = aulaMapper.toEntity(aulaRequestDTO);
        Aula aulaSalva = aulaService.adicionarAula(novaAula);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aulaMapper.toResponseDTO(aulaSalva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AulaRequestDTO aulaRequestDTO,
            @RequestParam Long professorId) {
        
        Aula aulaAtualizada = aulaMapper.toEntity(aulaRequestDTO);
        Aula aulaSalva = aulaService.atualizar(id, aulaAtualizada, professorId);
        return ResponseEntity.ok(aulaMapper.toResponseDTO(aulaSalva));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id, @RequestParam Long professorId) {
        aulaService.deletar(id, professorId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> buscarPorId(@PathVariable Long id) {
        var aula = aulaService.buscarPorId(id);
        return ResponseEntity.ok(aulaMapper.toResponseDTO(aula));
    }

    @PostMapping("/acessar")
    public ResponseEntity<AulaResponseDTO> acessar(
            @RequestParam Long alunoId,
            @RequestParam Long aulaId) {

        var aula = aulaService.acessarAula(alunoId, aulaId);
        return ResponseEntity.ok(aulaMapper.toResponseDTO(aula));
    }

    @PatchMapping("/{id}/concluir")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void concluir(
            @PathVariable Long id,
            @RequestParam Long alunoId) {

        aulaService.marcarComoConcluida(alunoId, id);
    }
}
