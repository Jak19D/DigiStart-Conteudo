package DigiStart_Conteudo.Service;

import DigiStart_Conteudo.Config.RabbitMQConfig;
import DigiStart_Conteudo.DTO.Input.ModuloRequestDTO;
import DigiStart_Conteudo.Model.Modulo;
import DigiStart_Conteudo.Repository.ModuloRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ModuloRepository moduloRepository;

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

    @RabbitListener(queues = RabbitMQConfig.CONTENT_QUEUE)
    public void handleContentEvent(Map<String, Object> contentEvent) {
        try {
            String eventType = (String) contentEvent.get("eventType");

            log.info("Recebido evento de conteúdo: eventType={}", eventType);

            switch (eventType) {
                case "CREATE_MODULE":
                    handleCreateModule(contentEvent);
                    break;
                case "LIST_MODULES":
                    handleListModules(contentEvent);
                    break;
                default:
                    log.warn("Tipo de evento de conteúdo não reconhecido: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de conteúdo: {}", e.getMessage(), e);
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

    private void handleCreateModule(Map<String, Object> event) {
        Long professorId = ((Number) event.get("professorId")).longValue();
        String nome = (String) event.get("nome");
        String descricao = (String) event.get("descricao");
        Boolean ativo = (Boolean) event.get("ativo");
        
        // Criar módulo no banco
        Modulo modulo = new Modulo();
        modulo.setNome(nome);
        modulo.setDescricao(descricao);
        modulo.setAtivo(ativo != null ? ativo : true);
        modulo.setProfessorId(professorId);
        
        moduloRepository.save(modulo);
        
        System.out.println("Módulo criado: " + nome + " para o professor: " + professorId);
    }

    private void handleListModules(Map<String, Object> event) {
        try {
            Long professorId = ((Number) event.get("professorId")).longValue();
            String replyTo = (String) event.get("replyTo");

            log.info("Listando módulos para professor: {}", professorId);

            List<Modulo> modulos = moduloService.listarPorProfessor(professorId);

            rabbitTemplate.convertAndSend("", replyTo, modulos);

            log.info("Enviados {} módulos para a fila de resposta: {}", modulos.size(), replyTo);

        } catch (Exception e) {
            log.error("Erro ao listar módulos: {}", e.getMessage(), e);
        }
    }
}