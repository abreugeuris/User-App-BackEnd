package com.backend.usersapp.services;

import com.backend.usersapp.models.entities.Role;
import java.util.Set;


public interface RoleService {
    Role findByName(String roleName);
    Set<Role> findAll();
}
