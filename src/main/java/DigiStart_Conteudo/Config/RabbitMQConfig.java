package DigiStart_Conteudo.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.host", matchIfMissing = false)
public class RabbitMQConfig {

    public static final String USER_QUEUE = "user.queue";
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String USER_ROUTING_KEY = "user.created";

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.send";

    public static final String CONTENT_QUEUE = "content.queue";
    public static final String CONTENT_EXCHANGE = "content.exchange";
    public static final String CONTENT_ROUTING_KEY = "content.created";

    public static final String CONTEUDO_QUEUE = "conteudo.queue";
    public static final String CONTEUDO_EXCHANGE = "conteudo.exchange";
    public static final String CONTEUDO_ROUTING_KEY = "conteudo.sync";

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE).build();
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder
                .bind(userQueue())
                .to(userExchange())
                .with(USER_ROUTING_KEY);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Queue contentQueue() {
        return QueueBuilder.durable(CONTENT_QUEUE).build();
    }

    @Bean
    public TopicExchange contentExchange() {
        return new TopicExchange(CONTENT_EXCHANGE);
    }

    @Bean
    public Binding contentBinding() {
        return BindingBuilder
                .bind(contentQueue())
                .to(contentExchange())
                .with(CONTENT_ROUTING_KEY);
    }

    @Bean
    public Queue conteudoQueue() {
        return QueueBuilder.durable(CONTEUDO_QUEUE).build();
    }

    @Bean
    public TopicExchange conteudoExchange() {
        return new TopicExchange(CONTEUDO_EXCHANGE);
    }

    @Bean
    public Binding conteudoBinding() {
        return BindingBuilder
                .bind(conteudoQueue())
                .to(conteudoExchange())
                .with(CONTEUDO_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
