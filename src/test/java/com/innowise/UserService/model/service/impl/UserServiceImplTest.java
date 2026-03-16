package com.innowise.UserService.model.service.impl;

import com.innowise.UserService.exception.BusinessException;
import com.innowise.UserService.exception.EntityNotFoundException;
import com.innowise.UserService.mapper.PaymentCardMapper;
import com.innowise.UserService.mapper.UserMapper;
import com.innowise.UserService.model.dao.PaymentCardDao;
import com.innowise.UserService.model.dao.UserDao;
import com.innowise.UserService.model.dto.PaymentCardDto;
import com.innowise.UserService.model.dto.UserDto;
import com.innowise.UserService.model.entity.PaymentCard;
import com.innowise.UserService.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PaymentCardDao paymentCardDao;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;
    private PaymentCardDto paymentCardDto;
    private PaymentCard paymentCard;


    @BeforeEach
    void setUp() {
        this.userDto = UserDto.builder()
                .id(1L)
                .name("Nikita")
                .surname("Hololeenko")
                .birthDate(LocalDate.of(2006, 3, 31))
                .email("hololeenko@gmail.com")
                .active(true)
                .build();

        this.user = User.builder()
                .id(1L)
                .name("Nikita")
                .surname("Hololeenko")
                .birthDate(LocalDate.of(2006, 3, 31))
                .email("hololeenko@gmail.com")
                .active(true)
                .build();

        this.paymentCardDto = PaymentCardDto.builder()
                .id(1L)
                .userId(1L)
                .number("9112999999999999")
                .holder("NIKITA HOLOLEENKO")
                .expirationDate(LocalDate.of(2028, 3, 31))
                .active(true)
                .build();

        this.paymentCard = PaymentCard.builder()
                .id(1L)
                .user(user)
                .number("9112999999999999")
                .holder("NIKITA HOLOLEENKO")
                .expirationDate(LocalDate.of(2028, 3, 31))
                .active(true)
                .build();
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests{

        @Test
        @DisplayName("Should create test when UserDto is exist ")
        void shouldCreateUserSuccessfully() {
            String email = "hololeenko@gmail.com";
            when(userDao.existsByEmail(email)).thenReturn(false);
            when(userMapper.toUser(userDto)).thenReturn(user);
            when(userDao.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.createUser(userDto);

            assertNotNull(result);
            assertEquals(email, result.getEmail());
            verify(userDao, times(1)).existsByEmail(email);
            verify(userMapper, times(1)).toUser(userDto);
            verify(userDao).save(argThat(user -> user.getEmail() != null && user.getEmail().equals(email)));
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Should throw BusinessException when userDto is null")
        void shouldThrowBusinessExceptionWhenUserDtoIsNull() {
            BusinessException businessException =  assertThrows(
                    BusinessException.class,
                    () -> userService.createUser(null));

            assertEquals("[createUser] UserDto is null",  businessException.getMessage());
            verifyNoInteractions(userDao);
        }

        @Test
        @DisplayName("Should throw BusinessException when user with the same email is exist")
        void shouldThrowBusinessExceptionWhenUserWithTheSameEmailIsExist() {
            when(userDao.existsByEmail(anyString())).thenReturn(true);

            BusinessException businessException =  assertThrows(
                    BusinessException.class,
                    () -> userService.createUser(userDto));

            assertEquals("User with this email already exists",  businessException.getMessage());
            verifyNoMoreInteractions(userDao);

        }
    }

    @Nested
    @DisplayName("Get User By Id Tests")
    class GetUserByIdTests{

        @Test
        @DisplayName("Should return user by id if user exist")
        void shouldReturnUserSuccessfully() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.getUserById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
            verify(userDao, times(1)).findUserById(id);
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Should throw BusinessException when id is null")
        void shouldThrowBusinessExceptionWhenIdIsNull() {
            Long id = null;

            BusinessException businessException =  assertThrows(
                    BusinessException.class,
                    () -> userService.getUserById(id));

            assertNotNull(businessException);
            assertEquals("[getUserById] Id is null",  businessException.getMessage());
            verifyNoInteractions(userDao);
            verifyNoInteractions(userMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user is not exist")
        void shouldThrowEntityNotFoundExceptionWhenUserIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.getUserById(id)
            );

            assertNotNull(entityNotFoundException);
            assertEquals("User with id [%s] not found".formatted(id),  entityNotFoundException.getMessage());
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    @DisplayName("Get User With Cards By Id Tests")
    class GetUserWithCardsByIdTests{

        @Test
        @DisplayName("Should return user with cards if user exist")
        void shouldReturnUserSuccessfully() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);
            when(paymentCardDao.findAllByUserId(id)).thenReturn(List.of(paymentCard));
            when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

            UserDto result = userService.getUserWithCardsById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals(userDto.getUserPaymentCards(), List.of(paymentCardDto));
            verify(userDao, times(1)).findUserById(id);
            verify(paymentCardDao, times(1)).findAllByUserId(id);
            verify(paymentCardMapper, times(1)).toPaymentCardDto(paymentCard);
        }

        @Test
        @DisplayName("Should throw BusinessException when id is null")
        void shouldThrowBusinessExceptionWhenIdIsNull() {
            Long id = null;

            BusinessException businessException =  assertThrows(
                    BusinessException.class,
                    () -> userService.getUserWithCardsById(id));

            assertNotNull(businessException);
            assertEquals("[getUserById] Id is null",  businessException.getMessage());
            verifyNoInteractions(userDao);
            verifyNoInteractions(userMapper);
            verifyNoInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException if user is not exist")
        void  shouldThrowEntityNotFoundExceptionIfUserIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.getUserWithCardsById(id)
            );

            assertNotNull(entityNotFoundException);
            assertEquals("User with id [%s] not found".formatted(id),  entityNotFoundException.getMessage());
            verify(userDao, times(1)).findUserById(id);
            verifyNoInteractions(userMapper);
            verifyNoInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);

        }
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests{

        @Test
        @DisplayName("Should return page of UserDto when getAllUsers is called")
        void getAllUsersShouldReturnPageOfDto() {
            String name = "Nikita";
            String surname = "Hololeenko";
            Pageable pageable = PageRequest.of(0, 15);

            List<User> users = List.of(user);
            Page<User> userPage = new PageImpl<>(users, pageable, users.size());


            when(userDao.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            Page<UserDto> result = userService.getAllUsers(name, surname, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(userDto.getName(), result.getContent().get(0).getName());

            verify(userDao).findAll(any(Specification.class), eq(pageable));
            verify(userMapper).toUserDto(any(User.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when pageable is null")
        void getAllUsersShouldThrowExceptionWhenPageableIsNull() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> userService.getAllUsers("name", "surname", null)
            );

            assertEquals("[getAllUsers] Pageable is null", exception.getMessage());
            verifyNoInteractions(userDao);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests{

        @Test
        @DisplayName("Should update user if userDto is not null")
        void updateUserShouldUpdateUserDto() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.updateUser(userDto);

            assertNotNull(result);
            assertEquals(id, result.getId());
            verify(userDao).findUserById(id);
            verify(userMapper).toUserDto(user);
            verify(userMapper).updateUserFromDto(userDto, user);
        }

        @Test
        @DisplayName("Should throw BusinessException when userDto is null")
        void shouldThrowBusinessExceptionWhenUserDtoIsNull() {
            userDto = null;

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> userService.updateUser(userDto));

            assertNotNull(businessException);
            assertEquals("[updateUser] UserDto is null", businessException.getMessage());
            verifyNoInteractions(userDao);
            verifyNoInteractions(userMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException if user is not exist")
        void  shouldThrowEntityNotFoundExceptionIfUserIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.updateUser(userDto)
            );

            assertNotNull(entityNotFoundException);
            assertEquals("User with id [%s] not found".formatted(id),  entityNotFoundException.getMessage());
            verify(userDao, times(1)).findUserById(id);
            verifyNoMoreInteractions(userDao);
            verifyNoInteractions(userMapper);


        }

        @Test
        @DisplayName("Should throw BusinessException when user is not active")
        void shouldThrowBusinessExceptionWhenUserIsNotActive() {
            Long id = 1L;
            user.setActive(false);
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> userService.updateUser(userDto));

            assertNotNull(businessException);
            assertEquals("User with id[%s] is not active".formatted(id), businessException.getMessage());
            verifyNoMoreInteractions(userDao);
            verifyNoInteractions(userMapper);
        }

    }

    @Nested
    @DisplayName("Activate User Tests")
    class ActivateUserTests{
        @Test
        @DisplayName("Should activate user successfully")
        void shouldActivateUserSuccessfully(){
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(userDao.activateUserById(id)).thenReturn(1);

            boolean success = userService.activateUserById(id);

            assertTrue(success);
            verify(userDao).findUserById(id);
            verify(userDao, times(1)).activateUserById(id);
        }

        @Test
        @DisplayName("Should throw BusinessException when id is null")
        void shouldThrowBusinessExceptionWhenIdIsNull() {
            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> userService.activateUserById(null));

            assertNotNull(businessException);
            assertEquals("[activateUserById] Id is null", businessException.getMessage());
            verifyNoInteractions(userDao);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException if user is not exist")
        void  shouldThrowEntityNotFoundExceptionIfUserIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.activateUserById(id)
            );

            assertNotNull(entityNotFoundException);
            assertEquals("User with id [%s] not found".formatted(id),  entityNotFoundException.getMessage());
            verify(userDao, times(1)).findUserById(id);
            verifyNoMoreInteractions(userDao);
        }
    }

    @Nested
    @DisplayName("Deactivate User Tests")
    class DeactivateUserTests{
        @Test
        @DisplayName("Should deactivate user successfully")
        void shouldDeactivateUserSuccessfully(){
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(userDao.deactivateUserById(id)).thenReturn(1);

            boolean success = userService.deactivateUserById(id);

            assertTrue(success);
            verify(userDao).findUserById(id);
            verify(userDao, times(1)).deactivateUserById(id);
        }

        @Test
        @DisplayName("Should throw BusinessException when id is null")
        void shouldThrowBusinessExceptionWhenIdIsNull() {
            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> userService.deactivateUserById(null));

            assertNotNull(businessException);
            assertEquals("[deactivateUserById] Id is null", businessException.getMessage());
            verifyNoInteractions(userDao);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException if user is not exist")
        void shouldThrowEntityNotFoundExceptionIfUserIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.deactivateUserById(id)
            );

            assertNotNull(entityNotFoundException);
            assertEquals("User with id [%s] not found".formatted(id),  entityNotFoundException.getMessage());
            verify(userDao, times(1)).findUserById(id);
            verifyNoMoreInteractions(userDao);
        }
    }
}