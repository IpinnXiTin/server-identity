package com.ipin.identity.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.ipin.identity.entity.Role;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
public class UserResponse {
    String id;
    String userName;
    String passWord;
    String firstName;
    String lastName;
    LocalDate dob;
    Set<Role> roles;
}
