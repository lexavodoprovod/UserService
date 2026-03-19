package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardDao;
import com.innowise.userservice.repository.UserDao;
import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardDao paymentCardDao;

    @Mock
    private UserDao userDao;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    private User user;
    private PaymentCardDto paymentCardDto;
    private PaymentCard paymentCard;

    @BeforeEach
    void setUp() {

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
    @DisplayName("Create Payment Card Tests")
    class CreatePaymentCardTests {

        @Test
        @DisplayName("Should create payment card if userDto exist")
        void shouldCreatePaymentCardSuccessfully() {
            String number = paymentCard.getNumber();
            Long userId = paymentCardDto.getUserId();
            when(paymentCardDao.existsByNumber(number)).thenReturn(false);
            when(userDao.findUserById(userId)).thenReturn(Optional.of(user));
            when(paymentCardDao.countActivePaymentCardByUserId(userId)).thenReturn(1);
            when(paymentCardMapper.toPaymentCard(paymentCardDto)).thenReturn(paymentCard);
            when(paymentCardDao.save(paymentCard)).thenReturn(paymentCard);
            when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

            PaymentCardDto result = paymentCardService.createPaymentCard(paymentCardDto);

            assertNotNull(result);
            assertEquals(number, result.getNumber());
            verify(paymentCardDao, times(1)).existsByNumber(number);
            verify(userDao, times(1)).findUserById(userId);
            verify(paymentCardDao, times(1)).countActivePaymentCardByUserId(userId);
            verify(paymentCardMapper, times(1)).toPaymentCard(paymentCardDto);
            verify(paymentCardDao, times(1)).save(paymentCard);
            verify(paymentCardMapper, times(1)).toPaymentCardDto(paymentCard);

        }

        @Test
        @DisplayName("Should throw BusinessException when card number already exists and card is active")
        void createPaymentCardShouldThrowExceptionWhenCardNumberExists() {
            when(paymentCardDao.existsByNumber(anyString())).thenReturn(true);
            when(paymentCardDao.findPaymentCardByNumber(anyString())).thenReturn(Optional.of(paymentCard));

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.createPaymentCard(paymentCardDto));

            assertNotNull(businessException);
            verifyNoMoreInteractions(paymentCardDao);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user not found")
        void createPaymentCard_ShouldThrowException_WhenUserNotFound() {
            Long userId = 1L;
            when(paymentCardDao.existsByNumber(anyString())).thenReturn(false);
            when(userDao.findUserById(userId)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> paymentCardService.createPaymentCard(paymentCardDto));

            assertNotNull(entityNotFoundException);
            verify(userDao, times(1)).findUserById(userId);
            verify(paymentCardDao, times(1)).existsByNumber(anyString());
            verifyNoMoreInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);
        }

        @Test
        @DisplayName("Should throw BusinessException when user is inactive")
        void createPaymentCard_ShouldThrowException_WhenUserInactive() {
            Long userId = 1L;
            user.setActive(false);
            when(paymentCardDao.existsByNumber(anyString())).thenReturn(false);
            when(userDao.findUserById(userId)).thenReturn(Optional.of(user));

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.createPaymentCard(paymentCardDto));

            assertNotNull(businessException);
            verifyNoMoreInteractions(paymentCardDao);
            verify(userDao, times(1)).findUserById(userId);
            verify(paymentCardDao, times(1)).existsByNumber(anyString());
            verifyNoInteractions(paymentCardMapper);
        }

        @Test
        @DisplayName("Should throw BusinessException when max card limit reached")
        void createPaymentCard_ShouldThrowException_WhenMaxLimitReached() {
            Long userId = 1L;
            String number = paymentCard.getNumber();

            when(paymentCardDao.existsByNumber(number)).thenReturn(false);
            when(userDao.findUserById(userId)).thenReturn(Optional.of(user));
            when(paymentCardDao.countActivePaymentCardByUserId(userId)).thenReturn(6);

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.createPaymentCard(paymentCardDto));


            assertNotNull(businessException);
            verify(userDao, times(1)).findUserById(userId);
            verify(paymentCardDao, times(1)).existsByNumber(number);
            verify(paymentCardDao, times(1)).countActivePaymentCardByUserId(userId);
            verifyNoMoreInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);
        }
    }

    @Nested
    @DisplayName("Get Payment Card By Id Tests")
    class GetPaymentCardByIdTests {

        @Test
        @DisplayName("Should return payment card by id successfully")
        void shouldReturnPaymentCardSuccessfully() {
            Long id = 1L;
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.of(paymentCard));
            when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

            PaymentCardDto result = paymentCardService.getPaymentCardById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
            verify(paymentCardDao, times(1)).findPaymentCardById(id);
            verify(paymentCardMapper, times(1)).toPaymentCardDto(paymentCard);
        }

        @Test
        @DisplayName("Should throw BusinessException when id id null")
        void shouldThrowExceptionWhenIdIsNull() {
            Long id = null;

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.getPaymentCardById(id)
            );

            assertNotNull(businessException);
            verifyNoInteractions(paymentCardDao);
            verifyNoMoreInteractions(paymentCardMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when payment card in not exist")
        void shouldThrowExceptionWhenPaymentCardInNotExist() {
            Long id = 1L;
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> paymentCardService.getPaymentCardById(id));

            assertNotNull(entityNotFoundException);
            verifyNoInteractions(paymentCardMapper);

        }
    }

    @Nested
    @DisplayName("Get All Payment Cards By User Id Tests")
    class GetAllPaymentCardsByUserIdTests {

        @Test
        @DisplayName("Should return list of cards bu user id successfully")
        void shouldReturnAllPaymentCardsSuccessfully() {
            Long userId = 1L;
            when(userDao.findUserById(userId)).thenReturn(Optional.of(user));
            when(paymentCardDao.findAllByUserId(userId)).thenReturn(List.of(paymentCard));
            when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

            List<PaymentCardDto> result = paymentCardService.getAllPaymentCardsByUserId(userId);

            assertNotNull(result);
            assertEquals(List.of(paymentCardDto), result);
            verify(userDao, times(1)).findUserById(userId);
            verify(paymentCardDao, times(1)).findAllByUserId(userId);
            verify(paymentCardMapper, times(1)).toPaymentCardDto(paymentCard);
        }

        @Test
        @DisplayName("Should throw BusinessException when id id null")
        void shouldThrowExceptionWhenIdIsNull() {
            Long id = null;

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.getAllPaymentCardsByUserId(id)
            );

            assertNotNull(businessException);
            verifyNoInteractions(paymentCardDao);
            verifyNoInteractions(userDao);
            verifyNoMoreInteractions(paymentCardMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user is not exist")
        void shouldThrowEntityNotFoundExceptionWhenUserIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> paymentCardService.getAllPaymentCardsByUserId(id));

            assertNotNull(entityNotFoundException);
            verifyNoInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);

        }
    }

    @Nested
    @DisplayName("Get All Payment CardsTests")
    class GetAllPaymentCardsTests {
        @Test
        @DisplayName("Should return page of PaymentCardDto when getAllPaymentCards is called")
        void getAllPaymentCardsSuccessfully() {
            String number = "9112";
            Pageable pageable = PageRequest.of(0, 15);

            List<PaymentCard> paymentCards = List.of(paymentCard);
            Page<PaymentCard> paymentCardPage = new PageImpl<>(paymentCards, pageable, paymentCards.size());


            when(paymentCardDao.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentCardPage);
            when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

            Page<PaymentCardDto> result = paymentCardService.getAllPaymentCards(number, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(paymentCardDto.getId(), result.getContent().get(0).getId());

            verify(paymentCardDao).findAll(any(Specification.class), eq(pageable));
            verify(paymentCardMapper).toPaymentCardDto(any(PaymentCard.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when pageable is null")
        void getAllPaymentCardsShouldThrowBusinessExceptionWhenPageableIsNull() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.getAllPaymentCards("9112", null)
            );

            assertNotNull(exception);
            verifyNoInteractions(paymentCardDao);
        }
    }

    @Nested
    @DisplayName("Update PaymentCard Tests")
    class UpdateUserTests {
        @Test
        @DisplayName("Should update payment card successfully ")
        void updatePaymentCardSuccessfully() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.of(paymentCard));
            when(paymentCardDao.save(any(PaymentCard.class))).thenReturn(paymentCard);
            when(paymentCardMapper.toPaymentCardDto(paymentCard)).thenReturn(paymentCardDto);

            PaymentCardDto result = paymentCardService.updatePaymentCard(paymentCardDto);

            assertNotNull(result);
            assertEquals(paymentCardDto.getId(), result.getId());
            verify(paymentCardDao).findPaymentCardById(id);
            verify(userDao).findUserById(id);
            verify(paymentCardMapper).toPaymentCardDto(paymentCard);
            verify(paymentCardMapper).updatePaymentCardFromDto(paymentCardDto, paymentCard);
            verify(paymentCardDao).save(paymentCard);

        }

        @Test
        @DisplayName("Should throw BusinessException when paymentCardDto is null")
        void shouldThrowExceptionWhenPaymentCardDtoIsNull() {

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.updatePaymentCard(null)
            );

            assertNotNull(businessException);
            verifyNoInteractions(paymentCardDao);
            verifyNoInteractions(userDao);
            verifyNoMoreInteractions(paymentCardMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user is not exist")
        void shouldThrowEntityNotFoundExceptionWhenUserIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> paymentCardService.updatePaymentCard(paymentCardDto));

            assertNotNull(entityNotFoundException);
            verify(userDao).findUserById(id);
            verifyNoInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);

        }

        @Test
        @DisplayName("Should throw BusinessException when user is not active")
        void shouldThrowBusinessExceptionWhenUserIsNotActive() {
            Long id = 1L;
            user.setActive(false);
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.updatePaymentCard(paymentCardDto));

            assertNotNull(businessException);
            verify(userDao).findUserById(id);
            verifyNoMoreInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when payment card is not exist")
        void shouldThrowEntityNotFoundExceptionWhenPaymentCardIsNotExist() {
            Long id = 1L;
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> paymentCardService.updatePaymentCard(paymentCardDto));

            assertNotNull(entityNotFoundException);
            verify(userDao).findUserById(id);
            verifyNoMoreInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);

        }

        @Test
        @DisplayName("Should throw BusinessException when payment card is not active")
        void shouldThrowBusinessExceptionWhenPaymentCardIsNotActive() {
            Long id = 1L;
            paymentCard.setActive(false);
            when(userDao.findUserById(id)).thenReturn(Optional.of(user));
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.of(paymentCard));


            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.updatePaymentCard(paymentCardDto));

            assertNotNull(businessException);
            verify(userDao, times(1)).findUserById(id);
            verify(paymentCardDao, times(1)).findPaymentCardById(id);
            verifyNoMoreInteractions(paymentCardDao);
            verifyNoInteractions(paymentCardMapper);
        }
    }

    @Nested
    @DisplayName("Activate User Tests")
    class ActivatePaymentCardTests {
        @Test
        @DisplayName("Should activate payment card successfully")
        void shouldActivatePaymentCardSuccessfully() {
            Long id = 1L;
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.of(paymentCard));
            when(paymentCardDao.countActivePaymentCardByUserId(id)).thenReturn(4);
            when(paymentCardDao.activatePaymentCardById(id)).thenReturn(1);
            when(cacheManager.getCache(anyString())).thenReturn(any(Cache.class));

            boolean success = paymentCardService.activatePaymentCardById(id);

            assertTrue(success);
            verify(paymentCardDao, times(1)).findPaymentCardById(id);
            verify(paymentCardDao, times(1)).countActivePaymentCardByUserId(id);
            verify(paymentCardDao, times(1)).activatePaymentCardById(id);
        }

        @Test
        @DisplayName("Should throw BusinessException when try activate 6 payment card")
        void shouldThrowBusinessExceptionWhenTryAdd6PaymentCard() {
            Long id = 1L;
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.of(paymentCard));
            when(paymentCardDao.countActivePaymentCardByUserId(id)).thenReturn(5);

            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.activatePaymentCardById(id));

            assertNotNull(businessException);
            verifyNoMoreInteractions(paymentCardDao);
        }

        @Test
        @DisplayName("Should throw BusinessException when id is null")
        void shouldThrowBusinessExceptionWhenIdIsNull() {
            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.activatePaymentCardById(null));

            assertNotNull(businessException);
            verifyNoInteractions(paymentCardDao);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException if payment card is not exist")
        void shouldThrowEntityNotFoundExceptionIfUserIsNotExist() {
            Long id = 1L;
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> paymentCardService.activatePaymentCardById(id)
            );

            assertNotNull(entityNotFoundException);
            verify(paymentCardDao, times(1)).findPaymentCardById(id);
            verifyNoMoreInteractions(paymentCardDao);
        }
    }


    @Nested
    @DisplayName("Deactivate User Tests")
    class DeactivateUserTests {
        @Test
        @DisplayName("Should deactivate payment card successfully")
        void shouldDeactivatePaymentCardSuccessfully() {
            Long id = 1L;
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.of(paymentCard));
            when(paymentCardDao.deactivatePaymentCardById(id)).thenReturn(1);
            when(cacheManager.getCache(anyString())).thenReturn(any(Cache.class));

            boolean success = paymentCardService.deactivatePaymentCardById(id);

            assertTrue(success);
            verify(paymentCardDao, times(1)).findPaymentCardById(id);
            verify(paymentCardDao, times(1)).deactivatePaymentCardById(id);
        }

        @Test
        @DisplayName("Should throw BusinessException when id is null")
        void shouldThrowBusinessExceptionWhenIdIsNull() {
            BusinessException businessException = assertThrows(
                    BusinessException.class,
                    () -> paymentCardService.deactivatePaymentCardById(null));

            assertNotNull(businessException);
            verifyNoInteractions(paymentCardDao);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException if payment card is not exist")
        void shouldThrowEntityNotFoundExceptionIfUserIsNotExist() {
            Long id = 1L;
            when(paymentCardDao.findPaymentCardById(id)).thenReturn(Optional.empty());

            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> paymentCardService.deactivatePaymentCardById(id)
            );

            assertNotNull(entityNotFoundException);
            verify(paymentCardDao, times(1)).findPaymentCardById(id);
            verifyNoMoreInteractions(paymentCardDao);
        }
    }
}