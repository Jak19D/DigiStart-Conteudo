package DigiStart_Conteudo.DTO.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModuloResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private Long professorId;
    private List<AulaResponseDTO> aulas;
}
