package com.backend.usersapp.controllers;

import com.backend.usersapp.models.dto.UserAppDto;
import com.backend.usersapp.models.entities.UserApp;
import com.backend.usersapp.services.UserService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author Geuris-Abreu-PC
 */
@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<UserAppDto> getAllUsers() {

        return userService.findAll();
    }

    @GetMapping("/pages/")
    public Page<UserAppDto> getAllUsers(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1 ,pageSize );
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserAppDto userAppDto = null;
        String error = null;

        try {
            userAppDto = userService.findById(id);

        } catch (Exception e) {
            error = e.getMessage();
            logger.error(e.getMessage());
        }
        return Objects.nonNull(userAppDto) ? ResponseEntity.ok(userAppDto)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserApp userApp, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        return new ResponseEntity<>(userService.save(userApp), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserApp userApp, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        return Objects.nonNull(userService.update(id, userApp))
                ? new ResponseEntity<>(userService.update(id, userApp), HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {

        String message = "Usuario eliminado de manera exitosa";
        HttpStatus status;
        try {
            userService.remove(id);
            status = HttpStatus.OK;

        } catch (Exception e) {
            message = e.getMessage();
            status = HttpStatus.NOT_FOUND;

        }
        return new ResponseEntity<>(message, status);
    }

    @GetMapping("/available")
    public ResponseEntity<Boolean> isNotAvailableUsernameOrEmail(@RequestParam String name, @RequestParam String value) {

        return new ResponseEntity<>(userService.isNotAvailableUsernameOrEmail(name, value), HttpStatus.OK);


    }


    private ResponseEntity<Map<String, String>> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> errors
                .put(err.getField(), err.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/api/auth/")
    public ResponseEntity<String> helloWord() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);

    }
}
