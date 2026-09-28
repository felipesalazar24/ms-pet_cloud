package cl.sanos_y_salvos.ms_base.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "pets")
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "age_category", length = 50, nullable = false)
    private String ageCategory;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "last_seen_location", length = 100)
    private String lastSeenLocation;

    @Column(name = "last_seen_date", nullable = false)
    private LocalDateTime lastSeenDate;

    @Column (name = "color", length = 30)
    private String color;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", length = 50, nullable = false)
    private String status;
}