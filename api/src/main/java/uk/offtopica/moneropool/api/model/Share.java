package uk.offtopica.moneropool.api.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "share")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "miner_id", nullable = false, updatable = false)
    private Miner miner;

    @Column(name = "difficulty", nullable = false, updatable = false)
    private Integer difficulty;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
