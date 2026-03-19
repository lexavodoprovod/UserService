package com.innowise.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCard extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 16)
    private String number;

    @Column(nullable = false)
    private String holder;

    @Column(name ="expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Builder.Default
    private boolean active = true;
}
