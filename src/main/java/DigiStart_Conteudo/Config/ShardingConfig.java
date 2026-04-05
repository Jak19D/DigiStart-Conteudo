package DigiStart_Conteudo.Config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(name = "spring.shardingsphere.enabled", havingValue = "true", matchIfMissing = false)
public class ShardingConfig {

    @PostConstruct
    public void logShardingEnabled() {
        System.out.println("ShardingSphere está habilitado para este microserviço");
    }
}
