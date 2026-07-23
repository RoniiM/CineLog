package com.cinelog.mapper;

import com.cinelog.dto.RegisterRequest;
import com.cinelog.dto.UserDto;
import com.cinelog.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    UserDto toDto(User user);
}
