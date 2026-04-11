package DigiStart_Conteudo.DTO.RabbitMQ;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentEventDTO {
    
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("professorId")
    private Long professorId;
    
    @JsonProperty("action")
    private String action;
    
    @JsonProperty("entityType")
    private String entityType;
    
    @JsonProperty("entityId")
    private Long entityId;
    
    @JsonProperty("timestamp")
    private Long timestamp;
    
    @JsonProperty("localDateTime")
    private LocalDateTime localDateTime;
    
    public ContentEventDTO() {
        this.localDateTime = LocalDateTime.now();
    }
    
    public ContentEventDTO(String eventType, Long professorId, String action, String entityType, Long entityId) {
        this();
        this.eventType = eventType;
        this.professorId = professorId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = System.currentTimeMillis();
    }
}
