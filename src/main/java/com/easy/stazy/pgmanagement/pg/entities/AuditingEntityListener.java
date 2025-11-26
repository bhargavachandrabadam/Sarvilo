package com.easy.stazy.pgmanagement.pg.entities;

import com.easy.stazy.pgmanagement.pg.enums.UserType;
import com.easy.stazy.shared.service.SecurityUserService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuditingEntityListener {
    private static final Logger logger = LoggerFactory.getLogger(AuditingEntityListener.class);

    @PrePersist
    public void prePersist(Object target) {
        System.out.println("[Audit] PrePersist called for: " + target.getClass().getSimpleName());
        logger.info("[Audit] PrePersist called for entity: {}", target.getClass().getSimpleName());
        if (target instanceof AuditableEntity entity) {
            SecurityUserService securityUserService = SpringContext.getBean(SecurityUserService.class);
            Long currentUserId = null;
            UserType currentUserType = null;
            try {
                String idStr = securityUserService.getCurrentUserId();
                if (idStr != null) currentUserId = Long.parseLong(idStr);
                String role = securityUserService.getRole();
                if (role != null) currentUserType = UserType.valueOf(role.toUpperCase());
                System.out.println("[Audit] Setting createdById=" + currentUserId + ", createdByType=" + currentUserType);
                logger.info("[Audit] Setting createdById={}, createdByType={}", currentUserId, currentUserType);
            } catch (Exception e) {
                System.out.println("[Audit] Exception in prePersist: " + e.getMessage());
                logger.warn("[Audit] Exception in prePersist: {}", e.getMessage(), e);
            }
            entity.setCreatedById(currentUserId);
            entity.setCreatedByType(currentUserType);
            entity.setUpdatedById(currentUserId);
            entity.setUpdatedByType(currentUserType);
        }
    }

    @PreUpdate
    public void preUpdate(Object target) {
        System.out.println("[Audit] PreUpdate called for: " + target.getClass().getSimpleName());
        logger.info("[Audit] PreUpdate called for entity: {}", target.getClass().getSimpleName());
        if (target instanceof AuditableEntity entity) {
            SecurityUserService securityUserService = SpringContext.getBean(SecurityUserService.class);
            Long currentUserId = null;
            UserType currentUserType = null;
            try {
                String idStr = securityUserService.getCurrentUserId();
                if (idStr != null) currentUserId = Long.parseLong(idStr);
                String role = securityUserService.getRole();
                if (role != null) currentUserType = UserType.valueOf(role.toUpperCase());
                System.out.println("[Audit] Setting updatedById=" + currentUserId + ", updatedByType=" + currentUserType);
                logger.info("[Audit] Setting updatedById={}, updatedByType={}", currentUserId, currentUserType);
            } catch (Exception e) {
                System.out.println("[Audit] Exception in preUpdate: " + e.getMessage());
                logger.warn("[Audit] Exception in preUpdate: {}", e.getMessage(), e);
            }
            entity.setUpdatedById(currentUserId);
            entity.setUpdatedByType(currentUserType);
        }
    }
}
