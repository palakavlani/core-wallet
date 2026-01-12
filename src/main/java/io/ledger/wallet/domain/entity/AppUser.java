package io.ledger.wallet.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.ledger.wallet.domain.enums.UserType;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_users") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; 

    @Enumerated(EnumType.STRING) // Stores "VIP" or "STANDARD" as text in DB
    @Column(nullable = false)
    private UserType type = UserType.STANDARD;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}