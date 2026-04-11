package DigiStart_Conteudo.DTO.Output;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO padronizado para respostas de erro")
public class ErrorResponseDTO {

    @Schema(description = "Código do status HTTP", example = "404")
    private Integer status;

    @Schema(description = "Tipo do erro", example = "Recurso não encontrado")
    private String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Módulo com ID 123 não encontrado")
    private String message;

    @Schema(description = "Timestamp do erro", example = "2026-04-11T10:06:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
