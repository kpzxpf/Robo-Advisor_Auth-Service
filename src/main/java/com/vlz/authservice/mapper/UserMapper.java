package com.vlz.authservice.mapper;

import com.vlz.authservice.dto.UserDto;
import com.vlz.authservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);
}