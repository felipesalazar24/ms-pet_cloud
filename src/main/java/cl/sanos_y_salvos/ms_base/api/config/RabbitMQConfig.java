package cl.sanos_y_salvos.ms_base.api.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange commandExchange() {
        return new DirectExchange("ex.sanosysalvos.command");
    }

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange("ex.sanosysalvos.events");
    }
}