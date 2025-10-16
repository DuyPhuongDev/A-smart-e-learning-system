package com.hcmut.lms.usermanagement.repository;

import com.hcmut.lms.usermanagement.model.entity.EndpointRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EndpointRegistryRepository extends JpaRepository<EndpointRegistry, UUID> {
    
    List<EndpointRegistry> findByServiceName(String serviceName);
    
    List<EndpointRegistry> findByIsActiveTrue();
    
    Optional<EndpointRegistry> findByEndpointAndHttpMethodAndServiceName(String endpoint, String httpMethod, String serviceName);
    
    boolean existsByEndpointAndHttpMethodAndServiceName(String endpoint, String httpMethod, String serviceName);
}

