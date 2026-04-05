package DigiStart_Conteudo.Controller;

import DigiStart_Conteudo.DTO.Input.ExercicioRequestDTO;
import DigiStart_Conteudo.DTO.Output.ExercicioResponseDTO;
import DigiStart_Conteudo.Mapper.ExercicioMapper;
import DigiStart_Conteudo.Model.Exercicio;
import DigiStart_Conteudo.Service.ExercicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercicios")
@RequiredArgsConstructor
public class ExercicioRestController {

    private final ExercicioService exercicioService;
    private final ExercicioMapper exercicioMapper;

    @GetMapping
    public ResponseEntity<List<ExercicioResponseDTO>> listar() {
        List<Exercicio> exercicios = exercicioService.listarTodos();
        List<ExercicioResponseDTO> dtos = exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExercicioResponseDTO> buscarPorId(@PathVariable Long id) {
        Exercicio exercicio = exercicioService.buscarPorId(id);
        return ResponseEntity.ok(exercicioMapper.toResponseDTO(exercicio));
    }

    @GetMapping("/aula/{aulaId}")
    public ResponseEntity<List<ExercicioResponseDTO>> listarPorAula(@PathVariable Long aulaId) {
        List<Exercicio> exercicios = exercicioService.listarPorAula(aulaId);
        List<ExercicioResponseDTO> dtos = exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ExercicioResponseDTO> adicionar(@Valid @RequestBody ExercicioRequestDTO dto,
                                                        @RequestParam Long professorId) {
        Exercicio exercicio = exercicioService.criarNovoExercicio(
                dto.getAulaId(),
                dto.getTitulo(),
                dto.getDescricao(),
                professorId
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exercicioMapper.toResponseDTO(exercicio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExercicioResponseDTO> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody ExercicioRequestDTO dto,
                                                         @RequestParam Long professorId) {
        Exercicio exercicioAtualizado = exercicioService.atualizar(
                id,
                dto.getTitulo(),
                dto.getDescricao(),
                professorId
        );
        return ResponseEntity.ok(exercicioMapper.toResponseDTO(exercicioAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @RequestParam Long professorId) {
        exercicioService.deletar(id, professorId);
        return ResponseEntity.noContent().build();
    }
}
