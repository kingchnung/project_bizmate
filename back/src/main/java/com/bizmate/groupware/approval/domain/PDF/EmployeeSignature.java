// com.bizmate.hr.domain.EmployeeSignature.java
package com.bizmate.groupware.approval.domain.PDF;

import com.bizmate.hr.domain.Employee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EMPLOYEE_SIGNATURE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 직원과 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "SIGN_IMAGE_PATH", nullable = false)
    private String signImagePath; // 예: C:/bizmate/signs/manager.png

    // ✅ 실제 로컬 경로는 서비스에서 계산됨
    public String getFullPath() {
        return System.getProperty("user.dir") + "/signatures/" + signImagePath;
    }

}
