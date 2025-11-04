package com.bizmate.hr.service;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.dto.user.UserPwChangeRequest;
import com.bizmate.hr.dto.user.UserUpdateRequestDTO;

import java.util.List;

public interface UserService {


    UserDTO createUserAccount(Employee employee, String initialPassword);
    UserDTO createUserAccount(Employee employee);

    UserDTO getUser(Long userId);

    UserDTO updateUser(Long userId, UserUpdateRequestDTO updateDTO);

    void deleteUser(Long userId);

    void changePw(Long userId, UserPwChangeRequest dto);
    String adminResetPassword(Long userId);

    void adminUnlockAccount(Long userId);
    List<UserDTO> getAllUsers();

    int processLoginFailure(String username);
    void processLoginSuccess(String username);
    void setUserActiveStatus(Long userId, String activeStatus);
}
