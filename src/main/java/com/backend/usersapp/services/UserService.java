
package com.backend.usersapp.services;

import java.sql.SQLException;
import java.util.List;

import com.backend.usersapp.models.dto.UserAppDto;
import com.backend.usersapp.models.entities.UserApp;

/**
 *
 * @author Geuris-Abreu-PC
 */

public interface UserService  {
    
    List<UserAppDto> findAll();
    UserAppDto findById(Long id) throws SQLException;
    UserAppDto save(UserApp userApp);
    UserAppDto update(Long id, UserApp userApp);
    void remove(Long id) throws SQLException;
    Boolean isNotAvailableUsernameOrEmail(String name, String value);
}
