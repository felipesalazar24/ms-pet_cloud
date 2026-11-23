package cl.sanos_y_salvos.ms_base.api.consumer;

import cl.sanos_y_salvos.ms_base.api.dto.PetEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PetKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PetKafkaConsumer.class);

    @KafkaListener(
            topics = "sanosysalvos.pets.events",
            groupId = "pets-analytics-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePetEvent(
            @Payload PetEventDTO event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        if (event == null || event.id() == null || event.eventType() == null || event.eventType().isBlank()) {
            log.warn("Payload inválido recibido en Kafka | partition={} offset={}", partition, offset);
            throw new IllegalArgumentException("Evento de mascota inválido: faltan campos obligatorios");
        }

        log.info("Evento Kafka procesado [pets-analytics-group] | partition={} offset={} type={} petId={} name={} species={} breed={} status={}",
                partition, offset, event.eventType(), event.id(), event.petName(),
                event.nameType(), event.breed(), event.status());
    }
}