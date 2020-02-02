package uk.offtopica.moneropool.api.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "block")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "height", nullable = false, updatable = false)
    private Integer height;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
