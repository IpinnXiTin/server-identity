package com.ipin.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipin.identity.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    
}
