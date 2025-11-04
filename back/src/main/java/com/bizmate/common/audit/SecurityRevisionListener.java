package com.bizmate.common.audit;

import com.bizmate.hr.security.UserPrincipal;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        RevInfo rev = (RevInfo) revisionEntity;

        Authentication auth = SecurityContextHolder.getContext() != null
                ? SecurityContextHolder.getContext().getAuthentication()
                : null;

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserPrincipal up) {
                rev.setModifierId(up.getUsername());
                rev.setModifierName(up.getEmpName());
                rev.setModifierFull(String.format("%s (%s)", up.getEmpName(), up.getUsername()));
                return;
            }
            // 비표준 principal
            String name = String.valueOf(principal);
            rev.setModifierId(name);
            rev.setModifierName(name);
            rev.setModifierFull(name);
        } else {
            rev.setModifierId("anonymous");
            rev.setModifierName("anonymous");
            rev.setModifierFull("anonymous");
        }
    }
}