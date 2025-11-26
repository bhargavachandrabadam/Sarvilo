package com.easy.stazy.shared.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Base entity class that provides common fields and functionality for all JPA entities.
 * This abstract class is designed to be extended by all entity classes in the application.
 * It automatically manages creation and update timestamps for all entities.
 */
@Setter
@Getter
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * The timestamp when the entity was initially created.
     * This field is automatically set when the entity is first persisted and cannot be updated.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the entity was last updated.
     * This field is automatically updated whenever the entity is modified.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback method that is automatically invoked before an entity is persisted.
     * Sets both the creation and update timestamps to the current date and time.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * JPA lifecycle callback method that is automatically invoked before an entity is updated.
     * Updates the 'updatedAt' timestamp to the current date and time.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
