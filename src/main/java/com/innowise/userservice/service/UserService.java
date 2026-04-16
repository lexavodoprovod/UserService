package com.innowise.userservice.service;

import com.innowise.userservice.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(String name, String surname, Pageable pageable);

    UserDto updateUser(UserDto userDto);

    boolean activateUserById(Long id);

    boolean deactivateUserById(Long id);

    boolean deleteUserById(Long id);
}
