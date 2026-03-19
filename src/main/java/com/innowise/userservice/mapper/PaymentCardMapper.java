package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.entity.PaymentCard;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDto toPaymentCardDto(PaymentCard paymentCard);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCard toPaymentCard(PaymentCardDto paymentCardDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updatePaymentCardFromDto(PaymentCardDto paymentCardDto, @MappingTarget PaymentCard paymentCard);
}
