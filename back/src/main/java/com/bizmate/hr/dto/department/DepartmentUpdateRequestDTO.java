package com.bizmate.hr.dto.department;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [부서 수정 요청 DTO]
 */
@Getter
@NoArgsConstructor
public class DepartmentUpdateRequestDTO {

    @Size(max = 50, message = "부서명은 50자 이하로 입력해야 합니다.")
    private String deptName;

    // 부서코드는 변경되지 않거나, 변경이 필요하다면 별도의 로직 필요

    private Long parentDeptId;

    private String isUsed; // 부서 활성화/비활성화 ('Y'/'N') 수정 가능


}