package uk.offtopica.moneropool.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(name = "paid", nullable = false, updatable = false)
    private Boolean paid;

    @Column(name = "orphaned", nullable = false, updatable = false)
    private Boolean orphaned;

    @Column(name = "expected_reward", nullable = false, updatable = false)
    @JsonProperty("expected_reward")
    private Long expectedReward;

    @Column(name = "difficulty", nullable = false, updatable = false)
    private Long difficulty;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
