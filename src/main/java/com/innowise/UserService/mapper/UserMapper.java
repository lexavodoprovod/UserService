package com.innowise.UserService.mapper;

import com.innowise.UserService.model.dto.UserDto;
import com.innowise.UserService.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentCards", ignore = true)
    User toUser(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentCards", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateUserFromDto(UserDto userDto, @MappingTarget User user);
}
