package com.ipin.identity.dto.response;

import java.util.Set;

import com.ipin.identity.entity.Permission;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
public class RoleResponse {
    String roleName;
    String roleDescription;
    Set<Permission> permissions;
}
