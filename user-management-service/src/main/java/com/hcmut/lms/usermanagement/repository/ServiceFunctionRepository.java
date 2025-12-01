package com.hcmut.lms.usermanagement.repository;

import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceFunctionRepository extends JpaRepository<ServiceFunction, UUID> {
    List<ServiceFunction> findBySystemServiceId(UUID systemServiceId);
}

