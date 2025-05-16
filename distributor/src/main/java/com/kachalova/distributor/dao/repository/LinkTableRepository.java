package com.kachalova.distributor.dao.repository;

import com.kachalova.distributor.dao.entity.LinkTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkTableRepository extends JpaRepository<LinkTable, UUID> {
    Optional<LinkTable> findByAnonymizedData_Id(UUID anonymizedId);
}

