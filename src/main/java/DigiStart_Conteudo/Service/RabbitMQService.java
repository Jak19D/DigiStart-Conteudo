package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Config.RabbitMQConfig;
import DigiStart_Conteudo.DTO.RabbitMQ.ContentEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendContentEvent(Long professorId, String action, String entityType, Long entityId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "CONTENT_EVENT");
            event.put("professorId", professorId);
            event.put("action", action);
            event.put("entityType", entityType);
            event.put("entityId", entityId);
            event.put("timestamp", System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONTEUDO_EXCHANGE,
                RabbitMQConfig.CONTEUDO_ROUTING_KEY,
                event
            );
            log.info("Evento de conteúdo enviado: professorId={}, action={}, entityType={}, entityId={}", 
                    professorId, action, entityType, entityId);
        } catch (Exception e) {
            log.error("Erro ao enviar evento de conteúdo: {}", e.getMessage());
        }
    }

    public void sendProfessorValidation(Long professorId) {
        try {
            Map<String, Object> validation = new HashMap<>();
            validation.put("eventType", "PROFESSOR_VALIDATION");
            validation.put("professorId", professorId);
            validation.put("timestamp", System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                "user.validation.request",
                validation
            );
            log.info("Requisição de validação de professor enviada: professorId={}", professorId);
        } catch (Exception e) {
            log.error("Erro ao enviar requisição de validação: {}", e.getMessage());
        }
    }
}
