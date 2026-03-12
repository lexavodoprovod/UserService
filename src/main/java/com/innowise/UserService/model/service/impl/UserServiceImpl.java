package com.innowise.UserService.model.service.impl;

import com.innowise.UserService.exception.BusinessException;
import com.innowise.UserService.exception.EntityNotFoundException;
import com.innowise.UserService.model.dao.UserDao;
import com.innowise.UserService.model.dto.UserDto;
import com.innowise.UserService.model.entity.User;
import com.innowise.UserService.mapper.UserMapper;
import com.innowise.UserService.model.service.UserService;
import com.innowise.UserService.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if(userDto == null){
            throw new BusinessException("[createUser] UserDto is null");
        }

        String email = userDto.getEmail();

        if(userDao.existsByEmail(email)){
            throw new BusinessException("User with this email already exists");
        }

        User user = userMapper.toUser(userDto);

        userDao.save(user);

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto getUserById(Long id) {
        if(id == null){
            throw new  BusinessException("[getUserById] Id is null");
        }

        User user = userDao.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public Page<UserDto> getAllUsers(String name, String surname, Pageable pageable) {

        if(name == null || surname == null || pageable == null){
            throw new  BusinessException("[getAllUsers] Pageable, name or surname is null");
        }

        Specification<User> userSpecification = UserSpecification.byNameAndSurname(name, surname);

        Page<User> userPage = userDao.findAll(userSpecification, pageable);

        return userPage.map(userMapper :: toUserDto);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {

      if(userDto == null){
          throw new BusinessException("[updateUser] UserDto is null");
      }

      Long id = userDto.getId();

      User user = userDao.findUserById(id)
              .orElseThrow(() -> new EntityNotFoundException("User", id));

      userMapper.updateUserFromDto(userDto, user);

      userDao.save(user);

      return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public boolean activateUserById(Long id) {
        if(id == null){
            throw new  BusinessException("[activateUserById] Id is null");
        }

        int success = userDao.activateUserById(id);

        return success != 0;
    }

    @Override
    @Transactional
    public boolean deactivateUserById(Long id) {
        if(id == null){
            throw new  BusinessException("[deactivateUserById] Id is null");
        }

        int success = userDao.deactivateUserById(id);

        return success != 0;
    }
}
