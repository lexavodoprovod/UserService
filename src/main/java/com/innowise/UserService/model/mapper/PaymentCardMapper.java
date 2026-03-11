package com.innowise.UserService.model.mapper;

import com.innowise.UserService.model.dto.PaymentCardDto;
import com.innowise.UserService.model.entity.PaymentCard;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDto toPaymentCardDto(PaymentCard paymentCard);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    PaymentCard toPaymentCard(PaymentCardDto paymentCardDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updatePaymentCardFromDto(PaymentCardDto paymentCardDto, @MappingTarget PaymentCard paymentCard);
}
