package com.kachalova.distributor.dao.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OriginalData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String birthDate;
    private String birthPlace;
    private String passport;
    private String address;
    private String phone;
    private String email;
    private String inn;
    private String snils;
    private String card;

}
