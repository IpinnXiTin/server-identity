package com.ipin.identity.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipin.identity.dto.request.PermissionRequest;
import com.ipin.identity.dto.response.PermissionResponse;
import com.ipin.identity.entity.Permission;
import com.ipin.identity.repository.PermissionRepository;

@Service
public class PermissionService {
    PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = Permission.builder()
            .perName(request.getPerName())
            .perDescription(request.getPerDescription())
            .build();

        permissionRepository.save(permission);

        return PermissionResponse.builder()
            .perName(request.getPerName())
            .perDescription(request.getPerDescription())
            .build();
    }

    public List<PermissionResponse> getPermissions() {
        return permissionRepository.findAll().stream()
            .map(permission -> PermissionResponse.builder()
                .perName(permission.getPerName())
                .perDescription(permission.getPerDescription())
                .build())
            .collect(Collectors.toList());
    }

    public void deletePermission(String perName) {
        permissionRepository.deleteById(perName);
    }
}
