package com.backend.usersapp.services;

import com.backend.usersapp.models.entities.User;

import java.sql.SQLException;
import java.util.Collections;

import com.backend.usersapp.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.service.spi.ServiceException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Geuris-Abreu-PC
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final UserService userService;


    public UserServiceImpl(UserRepository userRepository, @Lazy UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;

    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {

        try {
            List<User> usersList = (List<User>) userRepository.findAll();
            return usersList.stream().sorted(Comparator.comparingLong(User::getId)).
                    toList();
        } catch (Exception e) {
            logger.error("call method : findAll  errorMsg:{} , cause:{}", e.getMessage(), e.getCause());
            throw new ServiceException(e.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id)  {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            logger.error("call method : findById  errorMsg:{} , cause:{}", e.getMessage(), e.getCause());
            throw new ServiceException("Usuario no encontrado");
        }

    }

    @Override
    @Transactional
    public User save(User user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordHashed =  passwordEncoder.encode( user.getPassword());
        user.setPassword(passwordHashed);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User userRequest) {
        Optional<User> optionalUser ;
        try {
            optionalUser = userService.findById(id);

        } catch (SQLException e) {
            throw new ServiceException(e.getMessage());
        }


        if (optionalUser.isPresent()) {
            User userDb = optionalUser.orElseThrow();

            userDb.setUsername(userRequest.getUsername());
            userDb.setEmail(userRequest.getEmail());

            if (Objects.nonNull(userRequest.getPassword()) && !userRequest.getPassword().equals("noting")) {
                userDb.setPassword(userRequest.getPassword());
            }

            return userService.save(userDb);
        }
        return null;
    }

    @Override
    @Transactional
    public void remove(Long id)  {
        Optional<User> o = userRepository.findById(id);
        if (o.isPresent()) {
            userRepository.deleteById(id);
        } else {
            String message = String.format("EL usuario con el %d no existe.", id);
            throw new ServiceException(message);
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {

            String password = user.get().getPassword();
            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(), password, Collections.emptyList());
        } else {
            throw new UsernameNotFoundException("El usuario no existe");
        }

    }

}
