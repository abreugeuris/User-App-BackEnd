package com.backend.usersapp.services;

import com.backend.usersapp.models.entities.User;
import com.backend.usersapp.repositories.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Geuris-Abreu-PC
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        List<User> usersList = (List<User>) userRepository.findAll();
        return usersList.stream().sorted(Comparator.comparingLong(User::getId)).
                collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User userRequest) {
        Optional<User> optionalUser = this.findById(id);

        if (optionalUser.isPresent()) {
            User userDb = optionalUser.orElseThrow();

            userDb.setUsername(userRequest.getUsername());
            userDb.setEmail(userRequest.getEmail());

            if (Objects.nonNull(userRequest.getPassword()) && !userRequest.getPassword().equals("noting")) {
                userDb.setPassword(userRequest.getPassword());
            }

            return this.save(userDb);
        }
        return null;
    }

    @Override
    @Transactional
    public void remove(Long id) throws Exception {
        Optional<User> o = userRepository.findById(id);
        if (o.isPresent()) {
            userRepository.deleteById(id);
        } else {
            String message = String.format("EL usuario con el %d no existe.", id);
            throw new Exception(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isAvailableUsername(String username) {
        
        Optional<User> user =userRepository.findByUsername(username);
        return user.isPresent();

    }

}
