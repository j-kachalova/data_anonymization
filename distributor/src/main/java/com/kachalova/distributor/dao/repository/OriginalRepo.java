package com.kachalova.distributor.dao.repository;

import com.kachalova.distributor.dao.entity.OriginalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OriginalRepo extends JpaRepository<OriginalData, UUID> {
    Optional<OriginalData> findByEmail(String email); // ✅ корректный метод
}
