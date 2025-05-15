package com.kachalova.distributor.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class LinkTable {

    @Id
    private UUID originalId;

    @Column(unique = true)
    private UUID anonymizedId;

    // геттеры/сеттеры
}

