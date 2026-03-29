package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.model.dto.request.AssignServiceFunctionToRoleRequest;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.RolesServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.RolesServiceFunctionId;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.RolesServiceFunctionRepository;
import com.hcmut.lms.usermanagement.repository.ServiceFunctionRepository;
import com.hcmut.lms.usermanagement.service.RoleServiceFunctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceFunctionServiceImpl implements RoleServiceFunctionService {
    
    private final RoleRepository roleRepository;
    private final ServiceFunctionRepository serviceFunctionRepository;
    private final RolesServiceFunctionRepository rolesServiceFunctionRepository;
    
    @Override
    @Transactional
    public void assignServiceFunctionsToRole(AssignServiceFunctionToRoleRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));
        
        // Remove existing assignments
        rolesServiceFunctionRepository.deleteById_RolesId(request.getRoleId());
        
        // Add new assignments
        for (UUID serviceFunctionId : request.getServiceFunctionIds()) {
            ServiceFunction serviceFunction = serviceFunctionRepository.findById(serviceFunctionId)
                    .orElseThrow(() -> new ResourceNotFoundException("ServiceFunction", "id", serviceFunctionId));
            
            RolesServiceFunctionId id = new RolesServiceFunctionId(request.getRoleId(), serviceFunctionId);
            RolesServiceFunction rolesServiceFunction = RolesServiceFunction.builder()
                    .id(id)
                    .role(role)
                    .serviceFunction(serviceFunction)
                    .build();
            
            rolesServiceFunctionRepository.save(rolesServiceFunction);
        }
    }
    
    @Override
    @Transactional
    public void removeServiceFunctionFromRole(UUID roleId, UUID serviceFunctionId) {
        RolesServiceFunctionId id = new RolesServiceFunctionId(roleId, serviceFunctionId);
        rolesServiceFunctionRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UUID> getServiceFunctionIdsByRoleId(UUID roleId) {
        return rolesServiceFunctionRepository.findById_RolesId(roleId).stream()
                .map(rsf -> rsf.getId().getServiceFunctionsId())
                .collect(Collectors.toList());
    }
}

