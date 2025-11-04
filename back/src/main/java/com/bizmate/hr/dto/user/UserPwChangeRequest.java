package com.bizmate.hr.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPwChangeRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPw;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이여야합니다. ")
    private String newPw;
}
