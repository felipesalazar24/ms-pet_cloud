package cl.sanos_y_salvos.ms_base.api.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {
    
    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPetRegisteredEvent(String petName) {
        String message = "Nueva mascota registrada: " + petName;
        rabbitTemplate.convertAndSend("ex.sanosysalvos.events", "notify.pet.created", message);
        System.out.println("Evento publicado en RabbitMQ: " + message);
    }
}
