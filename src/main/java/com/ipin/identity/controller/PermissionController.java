package com.ipin.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipin.identity.dto.request.PermissionRequest;
import com.ipin.identity.dto.response.ApiResponse;
import com.ipin.identity.dto.response.PermissionResponse;
import com.ipin.identity.service.PermissionService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;


@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/permission")
public class PermissionController {
    PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping()
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        var permission = permissionService.createPermission(request);

        return ApiResponse.<PermissionResponse>builder()
            .code(1000)
            .message("Permission created successfully")
            .result(permission)
            .build();
    }
    
    @GetMapping()
    public ApiResponse<List<PermissionResponse>> getPermissions() {
        var permission = permissionService.getPermissions();

        return ApiResponse.<List<PermissionResponse>>builder()
            .code(1000)
            .message("Permissions fetched successfully")
            .result(permission)
            .build();
    }
    
    @DeleteMapping("/{perName}")
    public ApiResponse<Void> deletePermission(@PathVariable String perName) {
        permissionService.deletePermission(perName);

        return ApiResponse.<Void>builder()
            .code(1000)
            .message("Permission deleted successfully")
            .build();
    }
}
