package com.innowise.UserService.model.dao;

import com.innowise.UserService.model.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import static com.innowise.UserService.query.PaymentCardQuery.*;


import java.util.List;
import java.util.Optional;


@Repository
public interface PaymentCardDao extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    int MAX_COUNT_CARDS = 5;

    default PaymentCard savePaymentCard(PaymentCard paymentCard){
        long id = paymentCard.getUser().getId();
        long countOfCards = countPaymentCardByUserId(id);

        if(countOfCards >= MAX_COUNT_CARDS){
            return null;
        }
        return save(paymentCard);
    };

    Optional<PaymentCard> findPaymentCardById(Long id);

    List<PaymentCard> findAllByUserId(Long id);

    boolean existsByNumber(String number);

    @Modifying
    @Query(value = UPDATE_PAYMENT_CARD_BY_ID_NATIVE, nativeQuery = true)
    void updatePaymentCardById(@Param("paymentCard") PaymentCard paymentCard);

    @Modifying
    @Query(value = ACTIVATE_PAYMENT_CARD_BY_ID_JPQL)
    int activatePaymentCardById(@Param("id") Long id);

    @Modifying
    @Query(value = DEACTIVATE_PAYMENT_CARD_BY_ID_JPQL)
    int deactivatePaymentCardById(@Param("id") Long id);

    @Query(value = COUNT_PAYMENT_CARD_BY_USER_ID_JPQL)
    int countPaymentCardByUserId(@Param("userId")Long userId);
}
