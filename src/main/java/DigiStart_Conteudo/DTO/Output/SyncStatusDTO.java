package DigiStart_Conteudo.DTO.Output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para status de sincronização de conteúdo do professor")
public class SyncStatusDTO {

    @Schema(description = "ID do professor", example = "123")
    private Long professorId;

    @Schema(description = "Total de módulos do professor", example = "5")
    private Long totalModulos;

    @Schema(description = "Módulos ativos", example = "3")
    private Long modulosAtivos;

    @Schema(description = "Módulos inativos", example = "2")
    private Long modulosInativos;

    @Schema(description = "Total de aulas do professor", example = "15")
    private Long totalAulas;

    @Schema(description = "Aulas ativas", example = "12")
    private Long aulasAtivas;

    @Schema(description = "Aulas inativas", example = "3")
    private Long aulasInativas;

    @Schema(description = "Total de exercícios do professor", example = "8")
    private Long totalExercicios;

    @Schema(description = "Status de sincronização", example = "true")
    private Boolean sincronizado;

    @Schema(description = "Mensagem de status", example = "Conteúdo sincronizado corretamente")
    private String mensagem;
}
