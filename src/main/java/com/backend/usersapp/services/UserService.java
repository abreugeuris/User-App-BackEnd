
package com.backend.usersapp.services;

import java.sql.SQLException;
import java.util.List;
import com.backend.usersapp.models.entities.User;
import java.util.Optional;

/**
 *
 * @author Geuris-Abreu-PC
 */

public interface UserService  {
    
    List<User> findAll();
    Optional<User> findById(Long id) throws SQLException;
    User save(User user);
    User update(Long id, User user);
    void remove(Long id) throws SQLException;
    Boolean isNotAvailableUsernameOrEmail(String name, String value);
}
