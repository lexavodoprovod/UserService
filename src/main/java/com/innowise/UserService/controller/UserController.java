package com.innowise.UserService.controller;

import com.innowise.UserService.model.dto.UserDto;
import com.innowise.UserService.model.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private static final int PAGINATION_SIZE = 15;
    private static final String SORT_BY = "id";

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        UserDto user = userService.createUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user-with-cards/{id}")
    public ResponseEntity<UserDto> getUserWithCards(@PathVariable Long id){
        UserDto user = userService.getUserWithCardsById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY ) Pageable pageable){

        Page<UserDto> userPage = userService.getAllUsers(name, surname, pageable);

        return ResponseEntity.ok(userPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,@Valid @RequestBody UserDto userDto) {
        userDto.setId(id);

        UserDto updatedUser = userService.updateUser(userDto);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserDto> activateUser(@PathVariable Long id){
        boolean success = userService.activateUserById(id);

        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable Long id){
        boolean success = userService.deactivateUserById(id);

        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id){
        UserDto user = userService.deleteUserById(id);
        return ResponseEntity.ok(user);
    }

}
