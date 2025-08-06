package com.backend.usersapp.services;

import com.backend.usersapp.models.entities.Role;
import com.backend.usersapp.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository repo) {
        this.roleRepository = repo;

    }

    public Role findByName(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        return roleOptional.orElse(null);
    }
    public Set<Role> findAll() {
        return roleRepository.findAllBy();
    }
}
