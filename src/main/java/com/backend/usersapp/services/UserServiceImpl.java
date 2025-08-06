package com.backend.usersapp.services;

import com.backend.usersapp.models.dto.UserAppDto;
import com.backend.usersapp.models.entities.Role;
import com.backend.usersapp.models.entities.UserApp;

import java.util.*;

import com.backend.usersapp.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.service.spi.ServiceException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Geuris-Abreu-PC
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAppDto> findAll() {
        List<UserAppDto> users = new ArrayList<>();
        try {

            List<UserApp> usersList = (List<UserApp>) userRepository.findAll();

            usersList.forEach(user -> users.add(
                    UserAppDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .admin(user.getRoles().stream().anyMatch(r -> r.getName().equals(Role.ROLE_ADMIN)))
                            .email(user.getEmail())
                            .build()
            ));
            users.sort(Comparator.comparing(UserAppDto::getId));

            return users;
        } catch (Exception e) {
            logger.error("call method : findAll  errorMsg:{} , cause:{}", e.getMessage(), e.getCause());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserAppDto findById(Long id) {
        UserAppDto userAppDto;
        try {
            Optional<UserApp> o = getUserById(id);
            userAppDto = UserAppDto.builder()
                    .id(o.get().getId())
                    .username(o.get().getUsername())
                    .admin(o.get().isAdmin())
                    .email(o.get().getEmail())
                    .build();


        } catch (ServiceException e) {
            logger.error("call method : findById  errorMsg:{} , cause:{}", e.getMessage(), e.getCause());
            throw new ServiceException(e.getMessage());
        }
        return userAppDto;

    }

    @Override
    @Transactional
    public UserAppDto save(UserApp userRequest) {

        try {
            UserApp userApp = new UserApp();
            String passwordHashed = passwordEncoder.encode(userRequest.getPassword());

            userApp.setUsername(userRequest.getUsername());
            userApp.setEmail(userRequest.getEmail());
            userApp.setPassword(passwordHashed);
            userApp.setRoles(getRoles(userRequest));

            return saveOrUpdateUse(userApp);


        } catch (Exception e) {
            logger.error("call method : save errorMsg:{} , cause:{}", e.getMessage(), e.getCause());
        }
        return null;

    }

    @Override
    @Transactional
    public UserAppDto update(Long id, UserApp userRequest) {


//        try {
        Optional<UserApp> optionalUser = getUserById(id);
        UserApp userAppDb = optionalUser.orElseThrow();

        userAppDb.setAdmin(userAppDb.getRoles().stream()
                .anyMatch(role -> role.getName().equals(Role.ROLE_ADMIN)));

        validationsFieldsChanges(userRequest, userAppDb);

        return saveOrUpdateUse(userAppDb);

//        } catch (Exception e) {
//            logger.error("call method : update errorMsg:{} , cause:{}", e.getMessage(), e.getCause());
//        }

//        return null;
    }

    @Override
    @Transactional
    public void remove(Long id) {

        try {
            getUserById(id).ifPresent(userRepository::delete);
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isNotAvailableUsernameOrEmail(String name, String value) {
        switch (name) {
            case "username" -> {
                return userRepository.findByUsername(value).isPresent();
            }

            case "email" -> {
                return userRepository.findByEmail(value).isPresent();
            }
            default -> {
                return false;
            }
        }

    }

    private Optional<UserApp> getUserById(Long id) {
        Optional<UserApp> o = userRepository.findById(id);
        if (o.isEmpty()) {
            String message = String.format("EL usuario con el %d no existe.", id);
            throw new ServiceException(message);
        }
        return o;
    }

    private Set<Role> getRoles(UserApp userRequest) {
        if (userRequest.isAdmin()) {
            return new HashSet<>(roleService.findAll());
        }
        Role role = roleService.findByName(Role.ROLE_USER);
        return new HashSet<>(Set.of(role));
    }


    private void validationsFieldsChanges(UserApp userRequest, UserApp userFromDb) {
        if (Objects.nonNull(userFromDb) && Objects.nonNull(userRequest)) {
            if (!userRequest.getUsername().equals(userFromDb.getUsername())) {
                userFromDb.setUsername(userRequest.getUsername());
            } else if (!userRequest.getEmail().equalsIgnoreCase(userFromDb.getEmail())) {
                userFromDb.setEmail(userRequest.getEmail());
            } else if (userRequest.isAdmin() != userFromDb.isAdmin()) {
                userFromDb.getRoles().clear();
                userFromDb.setRoles(getRoles(userRequest));
            }

        }
    }

    private UserAppDto saveOrUpdateUse(UserApp userAppDb) {
        UserApp user = userRepository.save(userAppDb);
        return UserAppDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .admin(user.isAdmin())
                .build();
    }
}
