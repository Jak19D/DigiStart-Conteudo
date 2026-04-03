package DigiStart_Conteudo.Controller;

import DigiStart_Conteudo.DTO.Output.AulaResponseDTO;
import DigiStart_Conteudo.Mapper.AulaMapper;
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