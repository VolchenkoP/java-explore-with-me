package ru.practicum.users.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toUserEntity(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }
}
