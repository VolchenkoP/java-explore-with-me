package ru.practicum.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapToUserDto(User user);

    User mapToUser(UserDto userDto);
}
