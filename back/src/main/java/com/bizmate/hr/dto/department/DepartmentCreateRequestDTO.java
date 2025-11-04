package com.bizmate.hr.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [부서 등록 요청 DTO]
 */
@Getter
@NoArgsConstructor
public class DepartmentCreateRequestDTO {

    @NotBlank(message = "부서명은 필수입니다.")
    @Size(max = 50, message = "부서명은 50자 이하로 입력해야 합니다.")
    private String deptName;



    // 상위부서ID는 등록 시 선택 사항일 수 있습니다. (계층 구조 반영)
    private Long parentDeptId;

    // isUsed 필드는 등록 시 기본값 'Y'로 서비스 계층에서 자동 설정됩니다.
}