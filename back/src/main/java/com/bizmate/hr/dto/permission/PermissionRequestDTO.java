package com.bizmate.hr.dto.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequestDTO {
    @NotBlank(message = "권한 이름은 필수 입니다.")
    private String permName;
}
