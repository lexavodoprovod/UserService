package com.innowise.userservice.specification;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {

    private PaymentCardSpecification() {}

    public static Specification<PaymentCard> byNumber(String number) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (number != null && !number.isBlank()) {
                return criteriaBuilder.and(criteriaBuilder.like(root.get("number"), "%" + number + "%"));
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<PaymentCard> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<PaymentCard> byUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);

    }
}
