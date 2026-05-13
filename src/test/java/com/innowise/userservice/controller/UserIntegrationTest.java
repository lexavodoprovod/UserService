package com.innowise.userservice.controller;

import com.innowise.userservice.exception.userexception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.dto.UserDto;

import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;



class UserIntegrationTest extends BaseIT {



    @Autowired
    private UserMapper userMapper;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Roma")
            .surname("Dovidenko")
            .birthDate(LocalDate.of(2005, 9, 25))
            .email("dovidenko@gmail.com")
            .active(true)
            .build();


    @Nested
    @DisplayName("Create User Integration Tests")
    class CreateUserRequest {
        @Test
        @DisplayName("Should create user successfully")
        void addNewUserTest() throws Exception{
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        }

        @Test
        @DisplayName("Should return exception when name is empty")
        void ShouldReturnExceptionWhenNameIsEmpty() throws Exception{
            userDto.setName("");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        }

        @Test
        @DisplayName("Should return exception when surname is null")
        void ShouldReturnExceptionWhenSurnameIsNull() throws Exception{
            userDto.setSurname(null);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        }

        @Test
        @DisplayName("Should return exception when birthdate in future")
        void ShouldReturnExceptionWhenBirthDateInFuture() throws Exception{
            userDto.setBirthDate(LocalDate.of(2034, 9, 25));

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        }

        @Test
        @DisplayName("Should return exception when email is not valid")
        void ShouldReturnExceptionWhenEmailIsNotValid() throws Exception{
            userDto.setEmail("hololeenko234");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        }

        @Test
        @DisplayName("Should return exception when user with duplicate email")
        void ShouldReturnExceptionWhenUserWithDuplicateEmail() throws Exception{
            User user = userMapper.toUser(userDto);

           userDao.save(user);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Get User By Id Integration Tests")
    class GetUserByIdRequest {

        @Test
        @DisplayName("Should return User by id successfully")
        void getUserByIdTest() throws Exception{

            User user = userMapper.toUser(userDto);

            User savedUser = userDao.save(user);

            mockMvc.perform(MockMvcRequestBuilders
                    .get("/users/" + savedUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @DisplayName("Should return exception if user by id not exist")
        void shouldReturnExceptionIfUserByIdNotExist() throws Exception{

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Get Payment Cards By User Id Test")
    class GetPaymentCardsByUserIdTest {
        @Test
        @DisplayName("Should return list of cards for specific user")
        void shouldReturnCardsByUserId() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            PaymentCard card1 = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .active(false)
                    .build();

            PaymentCard card2 = PaymentCard.builder()
                    .number("5555666677778888")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2031, 5, 12))
                    .user(savedUser)
                    .build();

            paymentCardDao.save(card1);
            paymentCardDao.save(card2);


            mockMvc.perform(MockMvcRequestBuilders.get("/users/%s/payment-cards-all".formatted(savedUser.getId()))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].number").value("1111222233334444"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].number").value("5555666677778888"));
        }

        @Test
        @DisplayName("Should return empty list when user has no cards")
        void shouldReturnEmptyListWhenNoCards() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma.nocards@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            mockMvc.perform(MockMvcRequestBuilders.get("/users/%s/payment-cards-all".formatted(savedUser.getId())))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Get Active Payment Cards By User Id Test")
    class GetActivePaymentCardByUserIdTest{
        @Test
        @DisplayName("Should return list of active cards for specific user")
        void shouldReturnCardsByUserId() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            PaymentCard card1 = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .build();

            PaymentCard card2 = PaymentCard.builder()
                    .number("5555666677778888")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2031, 5, 12))
                    .user(savedUser)
                    .active(false)
                    .build();

            paymentCardDao.save(card1);
            paymentCardDao.save(card2);


            mockMvc.perform(MockMvcRequestBuilders.get("/users/%s/payment-cards".formatted(savedUser.getId()))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].number").value("1111222233334444"));
        }

        @Test
        @DisplayName("Should return empty list when user has no cards")
        void shouldReturnEmptyListWhenNoActiveCards() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma.nocards@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            PaymentCard card1 = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .active(false)
                    .build();

            PaymentCard card2 = PaymentCard.builder()
                    .number("5555666677778888")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2031, 5, 12))
                    .user(savedUser)
                    .active(false)
                    .build();

            paymentCardDao.save(card1);
            paymentCardDao.save(card2);

            mockMvc.perform(MockMvcRequestBuilders.get("/users/%s/payment-cards".formatted(savedUser.getId())))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Get All Users Integration Tests")
    class GetAllUsersRequest {

        @Test
        @DisplayName("Should return page of all users")
        void shouldReturnPageOfAllUsers() throws Exception {

            User roma = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            User ivan = User.builder()
                    .name("Ivan")
                    .surname("Ivanov")
                    .email("ivan@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(false)
                    .build();

            userDao.save(roma);
            userDao.save(ivan);


            mockMvc.perform(MockMvcRequestBuilders.get("/users")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("Should return filtered users by name and surname")
        void shouldReturnFilteredUsers() throws Exception {
            User roma = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            User anna = User.builder()
                    .name("Anna")
                    .surname("Dovidenko")
                    .email("anna@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            User ivan = User.builder()
                    .name("Ivan")
                    .surname("Ivanov")
                    .email("ivan@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            userDao.save(roma);
            userDao.save(anna);
            userDao.save(ivan);

            mockMvc.perform(MockMvcRequestBuilders.get("/users")
                            .param("surname", "Dovidenko")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].surname").value("Dovidenko"));

            mockMvc.perform(MockMvcRequestBuilders.get("/users")
                            .param("name", "Roma")
                            .param("surname", "Dovidenko"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Roma"));
        }
    }

    @Nested
    @DisplayName("Update User Integration Tests")
    class UpdateUserRequest {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() throws Exception {
            User user = User.builder()
                    .name("OldName")
                    .surname("OldSurname")
                    .email("old@example.com")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);
            Long userId = savedUser.getId();

            UserDto updateDto = UserDto.builder()
                    .name("NewName")
                    .surname("NewSurname")
                    .email("new@example.com")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .active(true)
                    .build();


            mockMvc.perform(MockMvcRequestBuilders.put("/users/" + userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NewName"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("new@example.com"));

            User updatedInDb = userDao.findById(userId).orElseThrow();
            assertEquals("NewName", updatedInDb.getName());
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent user")
        void shouldReturn404WhenUserNotFound() throws Exception {
            Long nonExistentId = 999L;


            mockMvc.perform(MockMvcRequestBuilders.put("/users/" + nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto)))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

    }

    @Nested
    @DisplayName("Activate User Integration Tests")
    class ActivateUserRequest {

        @Test
        @DisplayName("Should activate user successfully")
        void shouldActivateUserSuccessfully() throws Exception {
            User user = User.builder()
                    .name("Ivan")
                    .surname("Ivanov")
                    .email("ivan@test.com")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .active(false)
                    .build();
            User savedUser = userDao.save(user);
            Long userId = savedUser.getId();

            mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + userId + "/activate")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());

            User activatedUser = userDao.findById(userId).orElseThrow();
            assertTrue(activatedUser.isActive(), "User should be active in the database");
        }

        @Test
        @DisplayName("Should return 404 when activating non-existent user")
        void shouldReturn404WhenUserNotFound() throws Exception {
            Long nonExistentId = 999L;

            mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + nonExistentId + "/activate"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Deactivate User Integration Tests")
    class DeactivateUserRequest {

        @Test
        @DisplayName("Should deactivate user successfully")
        void shouldDeactivateUserSuccessfully() throws Exception {
            User user = User.builder()
                    .name("Ivan")
                    .surname("Ivanov")
                    .email("ivan@test.com")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .active(true)
                    .build();
            PaymentCard paymentCard1 = PaymentCard.builder()
                    .user(user)
                    .number("9112999999999999")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2028, 3, 31))
                    .active(true)
                    .build();

            PaymentCard paymentCard2 = PaymentCard.builder()
                    .user(user)
                    .number("9112991717999999")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2028, 3, 31))
                    .active(true)
                    .build();

            PaymentCard paymentCard3 = PaymentCard.builder()
                    .user(user)
                    .number("9046799999999999")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2028, 3, 31))
                    .active(true)
                    .build();

            User savedUser = userDao.save(user);
            Long userId = savedUser.getId();

            PaymentCard savedPaymentCard1 =  paymentCardDao.save(paymentCard1);
            Long card1Id = savedPaymentCard1.getId();

            PaymentCard savedPaymentCard2 = paymentCardDao.save(paymentCard2);
            Long card2Id = savedPaymentCard2.getId();

            PaymentCard savedPaymentCard3 =  paymentCardDao.save(paymentCard3);
            Long card3Id = savedPaymentCard3.getId();


            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/users/" + userId + "/deactivate")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());

            User deactivatedUser = userDao.findById(userId).orElseThrow();
            PaymentCard deactivatedCard1 = paymentCardDao.findPaymentCardById(card1Id).orElseThrow();
            PaymentCard deactivatedCard2 = paymentCardDao.findPaymentCardById(card2Id).orElseThrow();
            PaymentCard deactivatedCard3 = paymentCardDao.findPaymentCardById(card3Id).orElseThrow();

            assertFalse(deactivatedUser.isActive(), "User should be deactivated in the database");
            assertFalse(deactivatedCard1.isActive(), "PaymentCard1 should be deactivated in the database");
            assertFalse(deactivatedCard2.isActive(), "PaymentCard2 should be deactivated in the database");
            assertFalse(deactivatedCard3.isActive(), "PaymentCard3 should be deactivated in the database");
        }

        @Test
        @DisplayName("Should return 404 when activating non-existent user")
        void shouldReturn404WhenUserNotFound() throws Exception {
            Long nonExistentId = 999L;

            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/users/" + nonExistentId + "/deactivate"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Delete User Integration Tests")
    class DeleteUserRequest {
        @Test
        @DisplayName("Should delete user successfully")
        void  shouldDeleteUserSuccessfully() throws Exception {
            User user = User.builder()
                    .name("Ivan")
                    .surname("Ivanov")
                    .email("ivan@test.com")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .active(true)
                    .build();

            User savedUser = userDao.save(user);

            Long userId = savedUser.getId();

            mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());


            UserNotFoundException userNotFoundException = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.deleteUserById(userId)
            );

            assertNotNull(userNotFoundException);
        }

        @Test
        @DisplayName("Should return 404 when user non-exist")
        void  shouldReturn404WhenUserNotFound() throws Exception {
            Long userId = 999L;

            mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        }

    }




}
