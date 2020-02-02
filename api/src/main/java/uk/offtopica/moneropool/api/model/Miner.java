package uk.offtopica.moneropool.api.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "miner")
public class Miner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "wallet_address", nullable = false, updatable = false, unique = true)
    private String walletAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
