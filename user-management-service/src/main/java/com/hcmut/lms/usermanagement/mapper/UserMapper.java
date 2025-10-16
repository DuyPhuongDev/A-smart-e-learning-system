package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponseDto;
import com.hcmut.lms.usermanagement.model.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    
    @Mapping(target = "roles", source = "userRoles")
    UserResponseDto toResponseDto(User user);
    
    @Mapping(target = "roles", source = "userRoles")
    UserDetailResponseDto toDetailResponseDto(User user);
    
    List<UserResponseDto> toResponseDtoList(List<User> users);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    User toEntity(CreateUserRequestDto dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    void updateEntityFromDto(UpdateUserRequestDto dto, @MappingTarget User user);
}

