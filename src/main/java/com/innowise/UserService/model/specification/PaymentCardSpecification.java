package com.innowise.UserService.model.specification;

import com.innowise.UserService.model.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {

    private PaymentCardSpecification() {}

    public static Specification<PaymentCard> byNumber(Integer number) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (number != null) {
                return criteriaBuilder.and(criteriaBuilder.like(root.get("number"), "%" + number + "%"));
            }
            return criteriaBuilder.conjunction();
        };
    }
}
