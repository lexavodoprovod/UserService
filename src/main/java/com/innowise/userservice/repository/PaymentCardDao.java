package com.innowise.userservice.repository;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import static com.innowise.userservice.query.PaymentCardQuery.*;


import java.util.List;
import java.util.Optional;


@Repository
public interface PaymentCardDao extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    Optional<PaymentCard> findPaymentCardById(Long id);

    List<PaymentCard> findAllByUserId(Long id);

    List<PaymentCard> findAllByUserIdAndActiveTrue(Long id);

    boolean existsByNumber(String number);

    Optional<PaymentCard> findPaymentCardByNumber(String number);

    @Modifying
    @Query(value = ACTIVATE_PAYMENT_CARD_BY_ID_JPQL)
    int activatePaymentCardById(@Param("id") Long id);

    @Modifying
    @Query(value = DEACTIVATE_PAYMENT_CARD_BY_ID_JPQL)
    int deactivatePaymentCardById(@Param("id") Long id);

    @Modifying
    @Query(value = DEACTIVATE_PAYMENT_CARDS_BY_USER_ID_JPQL)
    int deactivateAllCardsByUserId(@Param("userId") Long userId);

    @Query(value = COUNT_PAYMENT_CARD_BY_USER_ID_JPQL)
    int countPaymentCardByUserId(@Param("userId")Long userId);

    @Query(value = COUNT_ACTIVE_PAYMENT_CARD_BY_USER_ID_JPQL)
    int countActivePaymentCardByUserId(@Param("userId")Long userId);
}
