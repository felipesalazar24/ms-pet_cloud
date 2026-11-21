package cl.sanos_y_salvos.ms_base.api.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record PetEventDTO (
    //Metadatos del evento KAFKA
    String eventId,
    String eventType,
    Instant eventTimestamp,

    // Datos de PetDTO
    Long id,
    String petName,
    String ageCategory,
    Long typeId,
    Long userId,
    String lastSeenLocation,
    LocalDateTime lastSeenDate,
    String status,

    // Datos de PetTypeDTO
    String nameType,
    String breed
) {}
