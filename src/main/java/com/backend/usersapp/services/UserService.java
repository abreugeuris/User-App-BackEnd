
package com.backend.usersapp.services;

import java.util.List;
import com.backend.usersapp.models.entities.User;
import java.util.Optional;

/**
 *
 * @author Geuris-Abreu-PC
 */

public interface UserService {
    
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    User update(Long id, User user);
    void remove(Long id) throws Exception;  
    Boolean isAvailableUsername(String username);
}
