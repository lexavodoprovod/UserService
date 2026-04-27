package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class PaymentCardIntegrationTest extends BaseIT {



    @Nested
    @DisplayName("Add Payment Card Test")
    class addPaymentCardTest {
        @Test
        @DisplayName("Should create payment card successfully")
        void shouldCreatePaymentCardSuccessfully() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            User savedUser = userDao.save(user);

            PaymentCardDto cardDto = PaymentCardDto.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 12, 31))
                    .userId(savedUser.getId())
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/payment-cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("1111222233334444"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(savedUser.getId()));

            assertEquals(1, paymentCardDao.countPaymentCardByUserId(savedUser.getId()));
        }

        @Test
        @DisplayName("Should return 409 when try to add 6 active card")
        void shouldReturn409WhenTryToAdd6ActiveCard() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            User savedUser = userDao.save(user);

            for(int i = 0; i < 5; i ++){
                PaymentCard card = PaymentCard.builder()
                        .number("111122223333444" + i)
                        .holder("ROMA DOVIDENKO")
                        .expirationDate(LocalDate.of(2030, 1, 1))
                        .user(user)
                        .build();

                paymentCardDao.save(card);
            }

            PaymentCard notActiveCard = PaymentCard.builder()
                    .number("1111222233334449")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(user)
                    .active(false)
                    .build();

            paymentCardDao.save(notActiveCard);

            PaymentCardDto cardDto = PaymentCardDto.builder()
                    .number("1100000033334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 12, 31))
                    .userId(savedUser.getId())
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/payment-cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isConflict());
        }


        @Test
        @DisplayName("Should return 400 when card number is too short")
        void shouldReturn400WhenCardNumberIsInvalid() throws Exception {
            PaymentCardDto invalidCard = PaymentCardDto.builder()
                    .number("123")
                    .holder("ROMA")
                    .expirationDate(LocalDate.of(2030, 12, 31))
                    .userId(1L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/payment-cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidCard)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Payment Card By Id Test")
    class GetPaymentCardByIdTest {
        @Test
        @DisplayName("Should return payment card by ID successfully")
        void shouldReturnPaymentCardById() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            userDao.save(user);

            PaymentCard card = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(user)
                    .build();

            PaymentCard savedCard = paymentCardDao.save(card);
            Long cardId = savedCard.getId();


            mockMvc.perform(MockMvcRequestBuilders.get("/payment-cards/" + cardId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(cardId))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("1111222233334444"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(user.getId()));
        }

        @Test
        @DisplayName("Should return 404 when payment card not found")
        void shouldReturn404WhenCardNotFound() throws Exception {
            Long nonExistentCardId = 999L;

            mockMvc.perform(MockMvcRequestBuilders.get("/payment-cards/" + nonExistentCardId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get All Active Payment Cards Test")
    class GetAllPaymentCardsTest {
        @Test
        @DisplayName("Should return page of active payment cards")
        void shouldReturnPageOfCards() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            for (int i = 0; i < 3; i++) {
                paymentCardDao.save(PaymentCard.builder()
                        .number("111122223333444" + i)
                        .holder("ROMA DOVIDENKO")
                        .expirationDate(LocalDate.of(2030, 1, 1))
                        .user(savedUser)
                        .build());
            }

            PaymentCard notActiveCard = PaymentCard.builder()
                    .number("1111222233334448")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .active(false)
                    .build();

            paymentCardDao.save(notActiveCard);

            mockMvc.perform(MockMvcRequestBuilders.get("/payment-cards")
                            .param("page", "0")
                            .param("size", "2")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3));
        }

        @Test
        @DisplayName("Should return filtered cards by number")
        void shouldReturnFilteredCards() throws Exception {
            User user = userDao.save(User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma.filter@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build());

            String targetNumber = "7777888899990000";
            paymentCardDao.save(PaymentCard.builder()
                    .number(targetNumber)
                    .holder("ROMA DOVIDENKO")
                    .user(user)
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .build());

            paymentCardDao.save(PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .user(user)
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .build());

            mockMvc.perform(MockMvcRequestBuilders.get("/payment-cards")
                            .param("number", targetNumber))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].number").value(targetNumber));
        }
    }

    @Nested
    @DisplayName("Update PaymentCard Tests")
    class UpdatePaymentCardTests {
        @Test
        @DisplayName("Should update payment card successfully")
        void shouldUpdatePaymentCardSuccessfully() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("update.card@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            PaymentCard card = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .build();
            PaymentCard savedCard = paymentCardDao.save(card);
            Long cardId = savedCard.getId();

            PaymentCardDto updateDto = PaymentCardDto.builder()
                    .number("9999888877776666")
                    .holder("ROMA NEW")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .userId(savedUser.getId())
                    .build();


            mockMvc.perform(MockMvcRequestBuilders.put("/payment-cards/" + cardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("9999888877776666"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.holder").value("ROMA NEW"));

            PaymentCard updatedInDb = paymentCardDao.findById(cardId).orElseThrow();
            assertEquals("9999888877776666", updatedInDb.getNumber());
        }

        @Test
        @DisplayName("Should return 400 when updating with invalid card number")
        void shouldReturn400WhenInvalidCardData() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("update.card@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            PaymentCard card = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .build();
            PaymentCard savedCard = paymentCardDao.save(card);

            PaymentCardDto invalidDto = PaymentCardDto.builder()
                    .number("123")
                    .holder("R")
                    .userId(savedUser.getId())
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/payment-cards/" + savedCard.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Activate PaymentCard Integration Tests")
    class ActivatePaymentCardRequest {

        @Test
        @DisplayName("Should throw exception when try activate 6 payment card")
        void shouldThrowExceptionWhenTryActivate6Card() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma.activate@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

           for(int i = 0; i < 5; i++) {
               PaymentCard card = PaymentCard.builder()
                       .number("111122223333444" + i)
                       .holder("ROMA DOVIDENKO")
                       .expirationDate(LocalDate.of(2030, 1, 1))
                       .user(savedUser)
                       .active(true)
                       .build();
               paymentCardDao.save(card);
           }
            PaymentCard card = PaymentCard.builder()
                    .number("111122223333365")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .active(false)
                    .build();

            PaymentCard savedCard = paymentCardDao.save(card);
            Long cardId = savedCard.getId();


            mockMvc.perform(MockMvcRequestBuilders.patch("/payment-cards/" + cardId + "/activate")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isConflict());
        }

        @Test
        @DisplayName("Should activate payment card successfully")
        void shouldActivatePaymentCardSuccessfully() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma.activate@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();
            User savedUser = userDao.save(user);

            PaymentCard card = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .active(false)
                    .build();
            PaymentCard savedCard = paymentCardDao.save(card);
            Long cardId = savedCard.getId();


            mockMvc.perform(MockMvcRequestBuilders.patch("/payment-cards/" + cardId + "/activate")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());


            PaymentCard activatedCard = paymentCardDao.findById(cardId).orElseThrow();
            assertTrue(activatedCard.isActive(), "Card should be active in DB");
        }

        @Test
        @DisplayName("Should return 404 when activating non-existent card")
        void shouldReturn404WhenCardNotFound() throws Exception {
            Long nonExistentId = 999L;

            mockMvc.perform(MockMvcRequestBuilders.patch("/payment-cards/" + nonExistentId + "/activate"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

    }

    @Nested
    @DisplayName("Deactivate PaymentCard Integration Tests")
    class DeactivatePaymentCardRequest {

        @Test
        @DisplayName("Should deactivate payment card successfully")
        void shouldDeactivatePaymentCardSuccessfully() throws Exception {
            User user = User.builder()
                    .name("Roma")
                    .surname("Dovidenko")
                    .email("roma.activate@example.com")
                    .birthDate(LocalDate.of(2005, 9, 25))
                    .active(true)
                    .build();

            User savedUser = userDao.save(user);

            PaymentCard card = PaymentCard.builder()
                    .number("1111222233334444")
                    .holder("ROMA DOVIDENKO")
                    .expirationDate(LocalDate.of(2030, 1, 1))
                    .user(savedUser)
                    .active(true)
                    .build();
            PaymentCard savedCard = paymentCardDao.save(card);
            Long cardId = savedCard.getId();


            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/payment-cards/" + cardId + "/deactivate")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());


            PaymentCard activatedCard = paymentCardDao.findById(cardId).orElseThrow();
            assertFalse(activatedCard.isActive(), "Card should be active in DB");
        }

        @Test
        @DisplayName("Should return 404 when deactivating non-existent card")
        void shouldReturn404WhenCardNotFound() throws Exception {
            Long nonExistentId = 999L;

            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/payment-cards/" + nonExistentId + "/deactivate"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

    }

}
