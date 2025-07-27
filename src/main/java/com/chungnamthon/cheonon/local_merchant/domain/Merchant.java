package com.chungnamthon.cheonon.local_merchant.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "merchant")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 반드시 명시!
    private Long id;

    @Column(name = "merchant_seq", nullable = false, unique = true)
    private String merchantSeq;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "tel", length = 30)
    private String tel;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_affiliate")
    private Boolean isAffiliate;      // 제휴 여부
}