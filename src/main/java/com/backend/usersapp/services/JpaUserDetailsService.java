package com.backend.usersapp.services;


import com.backend.usersapp.models.entities.UserApp;
import com.backend.usersapp.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<UserApp> userDb = userRepository.findByUsername(username);
        if (userDb.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User %s not found.", username));
        }
        UserApp user = userDb.get();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        user.getRoles().forEach(role ->
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName())));


        return new User(user.getUsername(), user.getPassword(),
                true,
                true,
                true,
                true,
                grantedAuthorities);

    }
}
