package com.duberlyguarnizo.plh.user;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toBasicEntity(UserBasicDto userBasicDto);

    UserBasicDto toBasicDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialBasicUpdate(UserBasicDto userBasicDto, @MappingTarget User user);

    User toRegisterEntity(UserRegisterDto userRegisterDto);

    UserRegisterDto toRegisterDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialRegisterUpdate(UserRegisterDto userRegisterDto, @MappingTarget User user);

    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);
}