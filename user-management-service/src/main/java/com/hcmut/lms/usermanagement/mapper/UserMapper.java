package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;
import com.hcmut.lms.usermanagement.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "student", ignore = true)
  @Mapping(target = "teacher", ignore = true)
  @Mapping(target = "admin", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User toEntity(CreateUserRequest request);

  @Mapping(target = "roleId", source = "role.id")
  @Mapping(target = "roleName", source = "role.name")
  @Mapping(target = "studentCode", source = "student.studentCode")
  @Mapping(target = "teacherCode", source = "teacher.teacherCode")
  @Mapping(target = "bio", source = "teacher.bio")
  @Mapping(target = "adminCode", source = "admin.adminCode")
  @Mapping(target = "intakeYearId", source = "student.intakeYearId")
  @Mapping(target = "departmentId", source = "student.departmentId")
  UserResponse toResponse(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "student", ignore = true)
  @Mapping(target = "teacher", ignore = true)
  @Mapping(target = "admin", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
