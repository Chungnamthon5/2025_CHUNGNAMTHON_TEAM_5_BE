package com.chungnamthon.cheonon.receipt_ocr.domain;

import com.chungnamthon.cheonon.map.domain.Merchant;
import com.chungnamthon.cheonon.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipt_preview")
@Getter
@Setter
@NoArgsConstructor
public class ReceiptPreview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → User 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // FK → Merchant 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "visit_datetime", nullable = false)
    private LocalDateTime visitDateTime;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

