package com.innowise.UserService.specification;

import com.innowise.UserService.model.entity.PaymentCard;
import com.innowise.UserService.model.entity.User;
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
        return (root, query, cb) -> cb.equal(root.get("active"), true);
    }
}
