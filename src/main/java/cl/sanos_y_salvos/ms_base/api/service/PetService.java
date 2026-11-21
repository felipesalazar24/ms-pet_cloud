package cl.sanos_y_salvos.ms_base.api.service;

import cl.sanos_y_salvos.ms_base.api.dto.PetDTO;
import cl.sanos_y_salvos.ms_base.api.dto.PetEventDTO;
import cl.sanos_y_salvos.ms_base.api.model.Pet;
import cl.sanos_y_salvos.ms_base.api.model.PetType;
import cl.sanos_y_salvos.ms_base.api.producer.PetKafkaProducer;
import cl.sanos_y_salvos.ms_base.api.producer.PetProducer;
import cl.sanos_y_salvos.ms_base.api.repository.PetRepository;
import cl.sanos_y_salvos.ms_base.api.repository.PetTypeRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetTypeRepository petTypeRepository;

    @Autowired
    private PetProducer petProducer; // RabbitMQ (se mantiene intacto)

    @Autowired
    private PetKafkaProducer petKafkaProducer; // Apache Kafka

    public PetDTO getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con el id: " + id));
        return entityToDto(pet);
    }

    public List<PetDTO> getAllPets() {
        return petRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PetDTO createPet(PetDTO petDto) {
        Pet pet = dtoToEntity(petDto);
        Pet savedPet = petRepository.save(pet);

        // RabbitMQ
        petProducer.sendNotification("Mascota registrada exitosamente: " + savedPet.getName());
        petProducer.sendAudit("AUDIT: Se creó una nueva mascota con ID " + savedPet.getId());

        // Apache Kafka
        petKafkaProducer.sendPetEvent(buildPetEvent("PET_CREATED", savedPet));

        return entityToDto(savedPet);
    }

    @Transactional
    public PetDTO updatePet(Long id, PetDTO petDto) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con el id: " + id));

        updateEntityFromDto(existingPet, petDto);
        Pet updatedPet = petRepository.save(existingPet);

        // RabbitMQ
        petProducer.sendAudit("AUDIT: Se actualizó la mascota con ID " + updatedPet.getId());

        // Apache Kafka
        petKafkaProducer.sendPetEvent(buildPetEvent("PET_UPDATED", updatedPet));

        return entityToDto(updatedPet);
    }

    @Transactional
    public void deletePet(Long id) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con el id: " + id));

        petRepository.deleteById(id);

        // RabbitMQ
        petProducer.sendCommand("CMD: Eliminar registros asociados a la mascota ID " + id);
        petProducer.sendAudit("AUDIT: Se eliminó la mascota con ID " + id);

        // Apache Kafka
        petKafkaProducer.sendPetEvent(buildPetEvent("PET_DELETED", existingPet));
    }

    private PetEventDTO buildPetEvent(String eventType, Pet pet) {
        PetType petType = (pet.getTypeId() != null)
                ? petTypeRepository.findById(pet.getTypeId()).orElse(null)
                : null;

        return new PetEventDTO(
                UUID.randomUUID().toString(),
                eventType,
                Instant.now(),
                pet.getId(),
                pet.getName(),
                pet.getAgeCategory(),
                pet.getTypeId(),
                pet.getUserId(),
                pet.getLastSeenLocation(),
                pet.getLastSeenDate(),
                pet.getStatus(),
                petType != null ? petType.getNameType() : null,
                petType != null ? petType.getBreed() : null
        );
    }

    private Pet dtoToEntity(PetDTO dto) {
        Pet entity = new Pet();
        entity.setName(dto.getName());
        entity.setAgeCategory(dto.getAgeCategory());
        entity.setTypeId(dto.getTypeId());
        entity.setUserId(dto.getUserId());
        entity.setLastSeenLocation(dto.getLastSeenLocation());
        entity.setLastSeenDate(dto.getLastSeenDate());
        entity.setColor(dto.getColor());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private PetDTO entityToDto(Pet entity) {
        PetDTO dto = new PetDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAgeCategory(entity.getAgeCategory());
        dto.setTypeId(entity.getTypeId());
        dto.setUserId(entity.getUserId());
        dto.setLastSeenLocation(entity.getLastSeenLocation());
        dto.setLastSeenDate(entity.getLastSeenDate());
        dto.setColor(entity.getColor());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    private void updateEntityFromDto(Pet entity, PetDTO dto) {
        entity.setName(dto.getName());
        entity.setAgeCategory(dto.getAgeCategory());
        entity.setTypeId(dto.getTypeId());
        entity.setUserId(dto.getUserId());
        entity.setLastSeenLocation(dto.getLastSeenLocation());
        entity.setLastSeenDate(dto.getLastSeenDate());
        entity.setColor(dto.getColor());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
    }
}