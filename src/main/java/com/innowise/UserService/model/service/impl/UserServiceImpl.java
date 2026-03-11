package com.innowise.UserService.model.service.impl;

import com.innowise.UserService.model.dao.UserDao;
import com.innowise.UserService.model.dto.UserDto;
import com.innowise.UserService.model.entity.User;
import com.innowise.UserService.model.mapper.UserMapper;
import com.innowise.UserService.model.service.UserService;
import com.innowise.UserService.model.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if(userDto == null){
            return null;
        }

        String email = userDto.getEmail();

        if(userDao.existsByEmail(email)){
            return null;
        }

        User user = userMapper.toUser(userDto);

        userDao.save(user);

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto getUserById(Long id) {
        if(id == null){
            return null;
        }

        Optional<User> userOpt = userDao.findUserById(id);

        if(userOpt.isPresent()){
            User user = userOpt.get();
            return userMapper.toUserDto(user);
        }

        return null;
    }

    @Override
    @Transactional
    public Page<UserDto> getAllUsers(String name, String surname, Pageable pageable) {

        Specification<User> userSpecification = UserSpecification.byNameAndSurname(name, surname);

        Page<User> userPage = userDao.findAll(userSpecification, pageable);

        return userPage.map(userMapper :: toUserDto);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {

      if(userDto == null){
          return null;
      }

      Long id = userDto.getId();
      Optional<User> userOpt = userDao.findUserById(id);

      if(userOpt.isPresent()){
          User user = userOpt.get();

          userMapper.updateUserFromDto(userDto, user);

          userDao.updateUserById(user);

          return userDto;
      }

        return null;
    }

    @Override
    @Transactional
    public boolean activateUserById(Long id) {
        if(id == null){
            return false;
        }

        int success = userDao.activateUserById(id);

        return success != 0;
    }

    @Override
    @Transactional
    public boolean deactivateUserById(Long id) {
        if(id == null){
            return false;
        }

        int success = userDao.deactivateUserById(id);

        return success != 0;
    }
}
