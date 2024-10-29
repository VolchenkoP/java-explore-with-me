package ru.practicum.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

@Mapper
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUserEntity(UserDto userDto);
}
