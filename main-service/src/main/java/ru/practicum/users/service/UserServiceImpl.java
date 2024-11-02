package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserEntity(userDto)));
    }

    @Override
    public Collection<UserDto> getUsers(Collection<Integer> ids, int from, int size) {
        validateFromAndSize(from, size);
        Sort sortDyId = Sort.by(Sort.Direction.ASC, "id");
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size, sortDyId);

        if (ids == null) {
            return getUsersWithoutIds(pageable);
        }
        return getUsersWithIds(ids, pageable);
    }

    @Override
    public void deleteUserById(long userId) {
        isUserExisted(userId);
        userRepository.deleteById(userId);
    }

    private Collection<UserDto> getUsersWithoutIds(Pageable pageable) {
        return userRepository.findAll(pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private Collection<UserDto> getUsersWithIds(Collection<Integer> ids, Pageable pageable) {
        return userRepository.findByIdIn(List.copyOf(ids), pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void validateFromAndSize(int from, int size) {
        if (from < 0 || size < 0) {
            log.warn("Incorrect from or size. From: {}, size: {}", from, size);
            throw new ValidationException("Bad from or size");
        }
    }

    private void isUserExisted(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Attempt to delete unknown user");
            throw new NotFoundException("User with id = " + userId + " was not found");
        }
    }
}
