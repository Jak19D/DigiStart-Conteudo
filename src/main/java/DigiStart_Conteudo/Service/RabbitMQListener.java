package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitMQListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQListener.class);

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private ExercicioService exercicioService;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserEvent(Map<String, Object> userEvent) {
        try {
            String eventType = (String) userEvent.get("eventType");
            Long userId = ((Number) userEvent.get("userId")).longValue();
            String userType = (String) userEvent.get("userType");
            String userEmail = (String) userEvent.get("userEmail");

            log.info("Recebido evento de usuário: eventType={}, userId={}, userType={}", 
                    eventType, userId, userType);

            switch (eventType) {
                case "USER_CREATED":
                    handleUserCreated(userId, userType, userEmail);
                    break;
                case "USER_UPDATED":
                    handleUserUpdated(userId, userType, userEmail);
                    break;
                case "USER_INACTIVE":
                case "USER_DELETED":
                    handleUserInactive(userId, userType);
                    break;
                case "USER_REACTIVATED":
                    handleUserReactivated(userId, userType);
                    break;
                default:
                    log.warn("Tipo de evento não reconhecido: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de usuário: {}", e.getMessage(), e);
        }
    }

    private void handleUserCreated(Long userId, String userType, String userEmail) {
        log.info("Processando criação de usuário: userId={}, userType={}, email={}", userId, userType, userEmail);
        
        if ("PROFESSOR".equals(userType)) {
            try {
                long modulosCount = moduloService.listarPorProfessor(userId).size();
                if (modulosCount > 0) {
                    log.info("Professor {} já possui {} módulos cadastrados", userId, modulosCount);
                } else {
                    log.info("Novo professor {} sem conteúdo cadastrado ainda", userId);
                }
            } catch (Exception e) {
                log.error("Erro ao verificar conteúdo do professor {}: {}", userId, e.getMessage());
            }
        }
    }

    private void handleUserUpdated(Long userId, String userType, String userEmail) {
        log.info("Processando atualização de usuário: userId={}, userType={}, email={}", userId, userType, userEmail);
        
        if ("PROFESSOR".equals(userType)) {
            try {
                long modulosAtivos = moduloService.listarTodosAtivos().stream()
                        .filter(modulo -> modulo.getProfessorId().equals(userId))
                        .count();
                
                log.info("Professor {} possui {} módulos ativos", userId, modulosAtivos);
            } catch (Exception e) {
                log.error("Erro ao verificar status dos módulos do professor {}: {}", userId, e.getMessage());
            }
        }
    }

    private void handleUserInactive(Long userId, String userType) {
        log.info("Processando inativação de usuário: userId={}, userType={}", userId, userType);
        
        if ("PROFESSOR".equals(userType)) {
            try {
                moduloService.desativarModulosPorProfessor(userId);
                aulaService.desativarAulasPorProfessor(userId);
                exercicioService.desativarExerciciosPorProfessor(userId);
                
                log.info("Conteúdos do professor {} desativados com sucesso", userId);
            } catch (Exception e) {
                log.error("Erro ao desativar conteúdos do professor {}: {}", userId, e.getMessage());
            }
        }
    }

    private void handleUserReactivated(Long userId, String userType) {
        log.info("Processando reativação de usuário: userId={}, userType={}", userId, userType);
        
        if ("PROFESSOR".equals(userType)) {
            try {
                long modulosInativos = moduloService.listarPorProfessor(userId).stream()
                        .filter(modulo -> !modulo.getAtivo())
                        .count();
                
                if (modulosInativos > 0) {
                    log.warn("Professor {} reativado possui {} módulos inativos. Reativação manual necessária.", userId, modulosInativos);
                } else {
                    log.info("Professor {} reativado não possui módulos inativos", userId);
                }
            } catch (Exception e) {
                log.error("Erro ao verificar módulos inativos do professor {}: {}", userId, e.getMessage());
            }
        }
    }
}
