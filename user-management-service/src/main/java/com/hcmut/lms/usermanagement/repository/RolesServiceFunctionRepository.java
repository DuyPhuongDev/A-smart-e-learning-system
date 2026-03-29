package com.hcmut.lms.usermanagement.repository;

import com.hcmut.lms.usermanagement.model.entity.RolesServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.RolesServiceFunctionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolesServiceFunctionRepository extends JpaRepository<RolesServiceFunction, RolesServiceFunctionId> {
    List<RolesServiceFunction> findById_RolesId(UUID roleId);
    List<RolesServiceFunction> findById_ServiceFunctionsId(UUID serviceFunctionId);
    void deleteById_RolesId(UUID roleId);
    void deleteById_ServiceFunctionsId(UUID serviceFunctionId);
}

