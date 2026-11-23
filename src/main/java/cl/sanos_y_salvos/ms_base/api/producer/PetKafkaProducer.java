package cl.sanos_y_salvos.ms_base.api.producer;

import cl.sanos_y_salvos.ms_base.api.dto.PetEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PetKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(PetKafkaProducer.class);
    public static final String TOPIC_PETS_EVENTS = "sanosysalvos.pets.events";

    private final KafkaTemplate<String, PetEventDTO> kafkaTemplate;

    public PetKafkaProducer(KafkaTemplate<String, PetEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPetEvent(PetEventDTO event) {
        String key = event.id() != null ? String.valueOf(event.id()) : "unknown";
        kafkaTemplate.send(TOPIC_PETS_EVENTS, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento Kafka enviado | topic={} partition={} offset={} eventType={} petId={}",
                                TOPIC_PETS_EVENTS,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.eventType(),
                                event.id());
                    } else {
                        log.error("Error al enviar evento Kafka | petId={} error={}", event.id(), ex.getMessage());
                    }
                });
    }
}