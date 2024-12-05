package com.backend.usersapp.controllers;

import com.backend.usersapp.models.entities.User;
import com.backend.usersapp.services.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Geuris-Abreu-PC
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getAllUsers() {

        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {

        Optional<User> userOptional = userService.findById(id);

        return userOptional.isPresent() ? ResponseEntity.ok(userOptional.orElseThrow())
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> Create(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return  validation(result);
        }
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return  validation(result);
        }
        return Objects.nonNull(userService.update(id, user))
                ? new ResponseEntity<>(userService.update(id, user), HttpStatus.CREATED)
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {

        String message = "Usuario eliminado de manera exitosa";
        HttpStatus status ;
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
    public ResponseEntity<?> isAvailableUsername(@RequestParam String username){      
        return new ResponseEntity<> ( userService.isAvailableUsername(username), HttpStatus.OK);
    }
    

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
//            errors.put(err.getField(), "El campo " + err.getField()
//                    + " " + err.getDefaultMessage());
            errors.put(err.getField(),  err.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

    }
}
