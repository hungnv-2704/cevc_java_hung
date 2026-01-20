package com.travel.tourbooking.mapper;

import com.travel.tourbooking.dto.UserResponse;
import com.travel.tourbooking.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public static List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }
}
