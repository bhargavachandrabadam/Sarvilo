package com.easy.stazy.pgmanagement.pg.entities;

import com.easy.stazy.pgmanagement.pg.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AuditableEntity {

    @Column(name = "created_by_id")
    protected Long createdById;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by_type")
    protected UserType createdByType;

    @Column(name = "updated_by_id")
    protected Long updatedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "updated_by_type")
    protected UserType updatedByType;

}
