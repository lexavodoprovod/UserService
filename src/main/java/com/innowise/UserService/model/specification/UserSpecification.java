package com.innowise.UserService.model.specification;

import com.innowise.UserService.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
public class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> byNameAndSurname(String firstName, String surname) {

        return (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = new ArrayList<>();

            if(firstName != null && !firstName.isBlank()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + firstName.toLowerCase() + "%"
                ));
            }

            if(surname != null && !surname.isBlank()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("surname")),
                        "%" + surname.toLowerCase() + "%"
                ));
            }

            if(predicates.isEmpty()){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
