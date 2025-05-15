package com.kachalova.distributor.dao.repository;

import com.kachalova.distributor.dao.entity.StartData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StartDataRepo  extends CrudRepository<StartData, Integer> {
    StartData findById(UUID id);
    StartData findByEmail(String email);
}
