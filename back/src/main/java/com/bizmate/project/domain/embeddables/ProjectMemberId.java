package com.bizmate.project.domain.embeddables;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class ProjectMemberId  implements Serializable {

    private Long projectId;
    private Long userId;

    public ProjectMemberId(Long projectId, Long userId){
        this.projectId = projectId;
        this.userId = userId;
    }


}
