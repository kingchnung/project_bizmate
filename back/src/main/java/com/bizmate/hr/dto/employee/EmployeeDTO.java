package com.bizmate.hr.dto.employee;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * [EmployeeDTO]
 * - 직원 정보 조회 응답(Response)용 DTO
 */
@Data
@Builder
public class EmployeeDTO {
    private Long empId;
    private String empNo;
    private String empName;
    private String deptName; // 부서명 (Dept 엔티티에서 추출)
    private Long deptId;
    private Long positionCode;    // 직급 ID
    private String positionName;
    private String gender;
    private Long gradeCode;
    private String gradeName;
    private String status; // 재직, 퇴사 등
    private String startDate;
    private String birthDate;
    private String leaveDate;

    private String phone;
    private String email;
    private String address;

    private Double careerYears; // ✅ 근속연수 (조회 시 자동 계산)

    /** Employee Entity를 DTO로 변환하는 팩토리 메서드 */
    public static EmployeeDTO fromEntity(Employee employee) {
        Position position = employee.getPosition();
        Grade grade = employee.getGrade();

        Double calculatedYears = null;
        if (employee.getStartDate() != null) {
            LocalDate now = LocalDate.now();
            Period diff = Period.between(employee.getStartDate(), now);
            calculatedYears = diff.getYears() + diff.getMonths() / 12.0;
            calculatedYears = Math.round(calculatedYears * 10) / 10.0; // 소수점 1자리까지
        }


        return EmployeeDTO.builder()
                .empId(employee.getEmpId())
                .empNo(employee.getEmpNo())
                .empName(employee.getEmpName())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .address(employee.getAddress())
                .gender(employee.getGender())
                .deptId(employee.getDepartment() != null ? employee.getDepartment().getDeptId() : null)
                .deptName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .positionCode(position != null ? position.getPositionCode() : null)
                .positionName(position != null ? position.getPositionName() : null)
                .gradeCode(grade != null ? grade.getGradeCode() : null)
                .gradeName(grade != null ? grade.getGradeName() : null)
                .status(employee.getStatus())
                .startDate(employee.getStartDate() != null
                        ? employee.getStartDate(). format(DateTimeFormatter.ISO_DATE)
                        : null)
                .birthDate(employee.getBirthDate() != null
                        ? employee.getBirthDate().format(DateTimeFormatter.ISO_DATE)
                        : null)
                .leaveDate(employee.getLeaveDate() != null
                        ? employee.getLeaveDate().format(DateTimeFormatter.ISO_DATE)
                        : null)
                .careerYears(calculatedYears)
                .build();
    }
}