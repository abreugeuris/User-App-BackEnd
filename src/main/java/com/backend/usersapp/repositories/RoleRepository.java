package com.backend.usersapp.repositories;

import com.backend.usersapp.models.entities.Role;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface  RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String roleName) ;
    Set<Role> findAllBy();
}

