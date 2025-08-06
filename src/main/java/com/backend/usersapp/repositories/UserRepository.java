
package com.backend.usersapp.repositories;

import com.backend.usersapp.models.entities.UserApp;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Geuris-Abreu-PC
 */
public interface UserRepository extends CrudRepository<UserApp, Long>{
    Optional<UserApp> findByUsername(String username);
    Optional<UserApp> findByEmail(String email);

    @Query("select u from UserApp u where u.username = ?1")
    Optional<UserApp> getUserByUsername(String username);

}
