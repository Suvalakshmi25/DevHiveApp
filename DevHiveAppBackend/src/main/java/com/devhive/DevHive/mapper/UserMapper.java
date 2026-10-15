package com.devhive.DevHive.mapper;

import com.devhive.DevHive.dto.membership.MemberResponse;
import com.devhive.DevHive.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public MemberResponse toMemberResponse(User user, String role) {
        return MemberResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(role)
                .build();
    }
}