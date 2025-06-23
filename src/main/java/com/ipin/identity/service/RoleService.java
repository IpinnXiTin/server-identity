package com.ipin.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipin.identity.dto.request.RoleRequest;
import com.ipin.identity.dto.response.RoleResponse;
import com.ipin.identity.entity.Permission;
import com.ipin.identity.entity.Role;
import com.ipin.identity.repository.PermissionRepository;
import com.ipin.identity.repository.RoleRepository;

@Service
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    
    public RoleResponse createRole(RoleRequest request) {
        var permissionsList = permissionRepository.findAllById(request.getPermissions());
        Set<Permission> permissionsSet = new HashSet<>(permissionsList);

        Role role = Role.builder()
            .roleName(request.getRoleName())
            .roleDescription(request.getRoleDescription())
            .permissions(permissionsSet)
            .build();

        roleRepository.save(role);

        return RoleResponse.builder()
            .roleName(role.getRoleName())
            .roleDescription(role.getRoleDescription())
            .permissions(role.getPermissions())
            .build();
    }

    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream()
            .map(role -> RoleResponse.builder()
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .permissions(role.getPermissions())
                .build())
            .collect(Collectors.toList());
    }

    public void deleteRole(String roleName) {
        roleRepository.deleteById(roleName);
    }
}
