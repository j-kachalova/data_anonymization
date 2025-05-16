package com.kachalova.distributor.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class LinkTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "original_id")
    private OriginalData originalData;
    @OneToOne
    @JoinColumn(name = "anonymized_id")
    private AnonymizedData anonymizedData;


    // геттеры/сеттеры
}

