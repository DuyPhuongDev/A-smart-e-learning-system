package com.hcmut.lms.usermanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.model.dto.request.AssignServiceFunctionToRoleRequest;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.RolesServiceFunction;
import com.hcmut.lms.usermanagement.model.entity.RolesServiceFunctionId;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.RolesServiceFunctionRepository;
import com.hcmut.lms.usermanagement.repository.ServiceFunctionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceFunctionServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ServiceFunctionRepository serviceFunctionRepository;

    @Mock
    private RolesServiceFunctionRepository rolesServiceFunctionRepository;

    @InjectMocks
    private RoleServiceFunctionServiceImpl roleServiceFunctionService;

    @Test
    void assignServiceFunctionsToRole_shouldAssign_whenValidRequest() {
        UUID roleId = UUID.randomUUID();
        UUID sfId1 = UUID.randomUUID();
        UUID sfId2 = UUID.randomUUID();
        AssignServiceFunctionToRoleRequest request = new AssignServiceFunctionToRoleRequest();
        request.setRoleId(roleId);
        request.setServiceFunctionIds(List.of(sfId1, sfId2));

        Role role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");

        ServiceFunction sf1 = new ServiceFunction();
        sf1.setId(sfId1);
        sf1.setName("fn1");

        ServiceFunction sf2 = new ServiceFunction();
        sf2.setId(sfId2);
        sf2.setName("fn2");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(serviceFunctionRepository.findById(sfId1)).thenReturn(Optional.of(sf1));
        when(serviceFunctionRepository.findById(sfId2)).thenReturn(Optional.of(sf2));

        roleServiceFunctionService.assignServiceFunctionsToRole(request);

        verify(rolesServiceFunctionRepository).deleteById_RolesId(roleId);
        verify(rolesServiceFunctionRepository, times(2)).save(any(RolesServiceFunction.class));
    }

    @Test
    void assignServiceFunctionsToRole_shouldThrowException_whenRoleNotFound() {
        UUID roleId = UUID.randomUUID();
        AssignServiceFunctionToRoleRequest request = new AssignServiceFunctionToRoleRequest();
        request.setRoleId(roleId);
        request.setServiceFunctionIds(List.of(UUID.randomUUID()));

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceFunctionService.assignServiceFunctionsToRole(request));
    }

    @Test
    void assignServiceFunctionsToRole_shouldThrowException_whenServiceFunctionNotFound() {
        UUID roleId = UUID.randomUUID();
        UUID sfId = UUID.randomUUID();
        AssignServiceFunctionToRoleRequest request = new AssignServiceFunctionToRoleRequest();
        request.setRoleId(roleId);
        request.setServiceFunctionIds(List.of(sfId));

        Role role = new Role();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(serviceFunctionRepository.findById(sfId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceFunctionService.assignServiceFunctionsToRole(request));
        verify(rolesServiceFunctionRepository).deleteById_RolesId(roleId);
    }

    @Test
    void removeServiceFunctionFromRole_shouldRemove_whenExists() {
        UUID roleId = UUID.randomUUID();
        UUID sfId = UUID.randomUUID();

        roleServiceFunctionService.removeServiceFunctionFromRole(roleId, sfId);

        verify(rolesServiceFunctionRepository).deleteById(any(RolesServiceFunctionId.class));
    }

    @Test
    void getServiceFunctionIdsByRoleId_shouldReturnIdList_whenRoleExists() {
        UUID roleId = UUID.randomUUID();
        UUID sfId1 = UUID.randomUUID();
        UUID sfId2 = UUID.randomUUID();

        RolesServiceFunction rsf1 = new RolesServiceFunction();
        rsf1.setId(new RolesServiceFunctionId(roleId, sfId1));

        RolesServiceFunction rsf2 = new RolesServiceFunction();
        rsf2.setId(new RolesServiceFunctionId(roleId, sfId2));

        when(rolesServiceFunctionRepository.findById_RolesId(roleId)).thenReturn(List.of(rsf1, rsf2));

        List<UUID> result = roleServiceFunctionService.getServiceFunctionIdsByRoleId(roleId);

        assertEquals(2, result.size());
        assertEquals(sfId1, result.get(0));
        assertEquals(sfId2, result.get(1));
    }

    @Test
    void getServiceFunctionIdsByRoleId_shouldReturnEmptyList_whenNoAssignments() {
        UUID roleId = UUID.randomUUID();

        when(rolesServiceFunctionRepository.findById_RolesId(roleId)).thenReturn(Collections.emptyList());

        List<UUID> result = roleServiceFunctionService.getServiceFunctionIdsByRoleId(roleId);

        assertEquals(0, result.size());
    }
}
