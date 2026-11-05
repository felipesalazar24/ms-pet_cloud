package cl.sanos_y_salvos.ms_base.api.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCommand(String message) {
        rabbitTemplate.convertAndSend("ex.sanosysalvos.command", "pet.command", message);
    }

    public void sendNotification(String message) {
        rabbitTemplate.convertAndSend("ex.sanosysalvos.events", "notify.pet.created", message);
        System.out.println("Evento publicado en RabbitMQ: " + message);
    }

    public void sendAudit(String message) {
        rabbitTemplate.convertAndSend("ex.sanosysalvos.events", "audit.pet", message);
    }
}