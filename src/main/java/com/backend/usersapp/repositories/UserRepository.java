
package com.backend.usersapp.repositories;

import com.backend.usersapp.models.entities.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Geuris-Abreu-PC
 */
public interface UserRepository extends CrudRepository<User, Long>{
    Optional<User> findByUsername(String username);
    
    
}
