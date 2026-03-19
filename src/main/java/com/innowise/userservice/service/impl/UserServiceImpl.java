package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.cardexception.CardDeactivateException;
import com.innowise.userservice.exception.userexception.*;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardDao;
import com.innowise.userservice.repository.UserDao;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.service.UserService;
import com.innowise.userservice.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PaymentCardDao paymentCardDao;
    private final UserMapper userMapper;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if (userDto == null) {
            throw new UserNullParameterException();
        }

        String email = userDto.getEmail();

        if (userDao.existsByEmail(email)) {
            throw new ExistUserException(email);
        }

        User user = userMapper.toUser(userDto);

        userDao.save(user);

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "users", key = "#id")
    public UserDto getUserById(Long id) {
        if (id == null) {
            throw new UserNullParameterException();
        }

        User user = userDao.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(String name, String surname, Pageable pageable) {

        if (pageable == null) {
            throw new UserNullParameterException();
        }

        Specification<User> userSpecification = Specification.
                where(UserSpecification.byNameAndSurname(name, surname)
                .and(UserSpecification.isActive()));

        Page<User> userPage = userDao.findAll(userSpecification, pageable);

        return userPage.map(userMapper::toUserDto);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "users", key = "#userDto.id")
    public UserDto updateUser(UserDto userDto) {

        if (userDto == null) {
            throw new UserNullParameterException();
        }

        Long id = userDto.getId();

        User user = userDao.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.isActive()) {
            throw new NotActiveUserException(id);
        }

        userMapper.updateUserFromDto(userDto, user);

        User updatedUser = userDao.save(user);

        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "users", key = "#id")
    public boolean activateUserById(Long id) {
        if (id == null) {
            throw new UserNullParameterException();
        }

        userDao.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        int result = userDao.activateUserById(id);

        return result != 0;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "users", key = "#id")
    public boolean deactivateUserById(Long id) {
        if (id == null) {
            throw new UserNullParameterException();
        }

        userDao.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        int userResult = userDao.deactivateUserById(id);

        if(userResult != 0){
            int cardResult = paymentCardDao.deactivateAllCardsByUserId(id);

            if(cardResult != 0){
                return true;
            }else{
                throw new CardDeactivateException(id);
            }
        }else{
            throw new UserDeactivateException(id);
        }
    }
}
