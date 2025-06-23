package com.ipin.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipin.identity.dto.request.RoleRequest;
import com.ipin.identity.dto.response.ApiResponse;
import com.ipin.identity.dto.response.RoleResponse;
import com.ipin.identity.service.RoleService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleController {
    RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    } 

    @PostMapping()
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        var role = roleService.createRole(request);

        return ApiResponse.<RoleResponse>builder()
            .code(1000)
            .message("Role created successfully")
            .result(role)
            .build();
    }
    
    @GetMapping()
    public ApiResponse<List<RoleResponse>> getRoles() {
        var role = roleService.getRoles();

        return ApiResponse.<List<RoleResponse>>builder()
            .code(1000)
            .message("Roles fetched successfully")
            .result(role)
            .build();
    }
    
    @DeleteMapping("/{roleName}")
    public ApiResponse<Void> deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);

        return ApiResponse.<Void>builder()
            .code(1000)
            .message("Role deleted successfully")
            .build();
    }
}
