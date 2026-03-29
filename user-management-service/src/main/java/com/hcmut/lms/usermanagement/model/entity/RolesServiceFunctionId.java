package com.hcmut.lms.usermanagement.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesServiceFunctionId implements Serializable {
    
    private UUID rolesId;
    
    private UUID serviceFunctionsId;
}

