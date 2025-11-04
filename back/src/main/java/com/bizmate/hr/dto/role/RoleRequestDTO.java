package com.bizmate.hr.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequestDTO{
    @NotBlank(message = "역할 이름은 필수 항목입니다.")
    private String roleName;
}
