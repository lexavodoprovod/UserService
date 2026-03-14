package com.innowise.UserService.model.service.impl;

import com.innowise.UserService.exception.BusinessException;
import com.innowise.UserService.exception.EntityNotFoundException;
import com.innowise.UserService.mapper.PaymentCardMapper;
import com.innowise.UserService.model.dao.PaymentCardDao;
import com.innowise.UserService.model.dao.UserDao;
import com.innowise.UserService.model.dto.PaymentCardDto;
import com.innowise.UserService.model.dto.UserDto;
import com.innowise.UserService.model.entity.PaymentCard;
import com.innowise.UserService.model.entity.User;
import com.innowise.UserService.mapper.UserMapper;
import com.innowise.UserService.model.service.UserService;
import com.innowise.UserService.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "users", key = "#id")
    public UserDto getUserById(Long id) {
        if(id == null){
            throw new  BusinessException("[getUserById] Id is null");
        }

        User user = userDao.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        return userMapper.toUserDto(user);
    }

    @Override
    @Cacheable(cacheNames = "users-with-cards", key = "#id")
    @Transactional(readOnly = true)
    public UserDto getUserWithCardsById(Long id){
        if(id == null){
            throw new  BusinessException("[getUserById] Id is null");
        }

        User user = userDao.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        UserDto userDto = userMapper.toUserDto(user);

        List<PaymentCard> paymentCards = paymentCardDao.findAllByUserId(id);

        List<PaymentCardDto> userCards = paymentCards.stream()
                .map(paymentCardMapper :: toPaymentCardDto)
                .toList();

        userDto.setUserPaymentCards(userCards);

        return userDto;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(String name, String surname, Pageable pageable) {

        if(pageable == null){
            throw new  BusinessException("[getAllUsers] Pageable is null");
        }

        Specification<User> userSpecification = UserSpecification.byNameAndSurname(name, surname);

        Page<User> userPage = userDao.findAll(userSpecification, pageable);

        return userPage.map(userMapper :: toUserDto);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "users", key = "#userDto.id")
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
    @CacheEvict(cacheNames = "users", key = "#id")
    public boolean activateUserById(Long id) {
        if(id == null){
            throw new  BusinessException("[activateUserById] Id is null");
        }

        int success = userDao.activateUserById(id);

        return success != 0;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "users", key = "#id")
    public boolean deactivateUserById(Long id) {
        if(id == null){
            throw new  BusinessException("[deactivateUserById] Id is null");
        }

        int success = userDao.deactivateUserById(id);

        return success != 0;
    }


    @Override
    @CacheEvict(cacheNames = "users", key = "#id")
    public UserDto deleteUserById(Long id) {
        if(id == null){
            throw new  BusinessException("[deleteUserById] Id is null");
        }

        User user = userDao.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        userDao.delete(user);

        return userMapper.toUserDto(user);
    }
}
