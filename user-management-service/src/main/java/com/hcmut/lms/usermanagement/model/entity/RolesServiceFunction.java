package com.hcmut.lms.usermanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles_service_functions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesServiceFunction {
    
    @EmbeddedId
    private RolesServiceFunctionId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolesId")
    @JoinColumn(name = "roles_id")
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceFunctionsId")
    @JoinColumn(name = "service_functions_id")
    private ServiceFunction serviceFunction;
}

