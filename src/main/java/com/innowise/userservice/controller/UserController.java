package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.service.PaymentCardService;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private static final int PAGINATION_SIZE = 15;
    private static final String SORT_BY = "id";

    private final UserService userService;
    private final PaymentCardService paymentCardService;

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

    @GetMapping("/{id}/payment-cards-all")
    public ResponseEntity<Page<PaymentCardDto>> getAllPaymentCardsByUserId(
            @PathVariable Long id,
            @RequestParam(required = false) String number,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY ) Pageable pageable
    ) {

        Page<PaymentCardDto> paymentCards = paymentCardService.getAllPaymentCardsByUserId(id, number,pageable);
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping("/{id}/payment-cards")
    public ResponseEntity<List<PaymentCardDto>> getAllActiveCardsByUserId(
            @PathVariable Long id
    ){
        List<PaymentCardDto> activeCardsPage = paymentCardService.getAllActiveCardsByUserId(id);

        return ResponseEntity.ok(activeCardsPage);
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
    public ResponseEntity<Void> activateUser(@PathVariable Long id){
        boolean success = userService.activateUserById(id);

        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id){
        boolean success = userService.deactivateUserById(id);

        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        boolean success = userService.deleteUserById(id);

        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
