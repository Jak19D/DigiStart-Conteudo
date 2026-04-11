package DigiStart_Conteudo.Controller;

import DigiStart_Conteudo.DTO.Output.SyncStatusDTO;
import DigiStart_Conteudo.Service.ModuloService;
import DigiStart_Conteudo.Service.AulaService;
import DigiStart_Conteudo.Service.ExercicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@Tag(name = "Sincronização", description = "Endpoints para verificação de sincronização entre microserviços")
public class SyncController {

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private ExercicioService exercicioService;

    @GetMapping("/status/professor/{professorId}")
    @Operation(summary = "Verificar status de sincronização do professor", description = "Retorna informações sobre o conteúdo do professor e status de sincronização")
    public ResponseEntity<SyncStatusDTO> verificarStatusProfessor(@PathVariable Long professorId) {
        try {
            SyncStatusDTO status = new SyncStatusDTO();
            
            // Contar módulos do professor
            long totalModulos = moduloService.listarPorProfessor(professorId).size();
            long modulosAtivos = moduloService.listarTodosAtivos().stream()
                    .filter(modulo -> modulo.getProfessorId().equals(professorId))
                    .count();
            
            // Contar aulas do professor
            long totalAulas = aulaService.listar().stream()
                    .filter(aula -> aula.getModulo().getProfessorId().equals(professorId))
                    .count();
            long aulasAtivas = aulaService.listar().stream()
                    .filter(aula -> aula.getModulo().getProfessorId().equals(professorId) && aula.isAtiva())
                    .count();
            
            // Contar exercícios do professor
            long totalExercicios = exercicioService.listarTodos().stream()
                    .filter(exercicio -> exercicio.getAula().getModulo().getProfessorId().equals(professorId))
                    .count();
            
            status.setProfessorId(professorId);
            status.setTotalModulos(totalModulos);
            status.setModulosAtivos(modulosAtivos);
            status.setModulosInativos(totalModulos - modulosAtivos);
            status.setTotalAulas(totalAulas);
            status.setAulasAtivas(aulasAtivas);
            status.setAulasInativas(totalAulas - aulasAtivas);
            status.setTotalExercicios(totalExercicios);
            status.setSincronizado(true);
            
            if (totalModulos > 0 && modulosAtivos == 0) {
                status.setMensagem("Professor possui conteúdo mas tudo está inativo - possível problema de sincronização");
                status.setSincronizado(false);
            } else if (totalModulos == 0) {
                status.setMensagem("Professor não possui conteúdo cadastrado");
            } else {
                status.setMensagem("Conteúdo do professor sincronizado corretamente");
            }
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            SyncStatusDTO errorStatus = new SyncStatusDTO();
            errorStatus.setProfessorId(professorId);
            errorStatus.setSincronizado(false);
            errorStatus.setMensagem("Erro ao verificar status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorStatus);
        }
    }

    @PostMapping("/reativar/professor/{professorId}")
    @Operation(summary = "Reativar conteúdo do professor", description = "Reativa todos os módulos, aulas e exercícios de um professor")
    public ResponseEntity<Map<String, Object>> reativarConteudoProfessor(@PathVariable Long professorId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Esta funcionalidade seria implementada no futuro
            // Por enquanto, apenas retorna informações sobre o que seria reativado
            
            long modulosInativos = moduloService.listarPorProfessor(professorId).stream()
                    .filter(modulo -> !modulo.getAtivo())
                    .count();
            
            response.put("professorId", professorId);
            response.put("modulosParaReativar", modulosInativos);
            response.put("mensagem", "Funcionalidade de reativação em desenvolvimento. Use os endpoints específicos para reativar conteúdo.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("erro", "Erro ao preparar reativação: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
