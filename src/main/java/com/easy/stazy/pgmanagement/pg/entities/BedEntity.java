package com.easy.stazy.pgmanagement.pg.entities;

import com.easy.stazy.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "beds")
public class BedEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    private String bedNumber;
    private String description;
    private BigDecimal monthlyRent;

    private Boolean underMaintenance=false;
    private Boolean isDeleted = false;
    @OneToMany(mappedBy = "bed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BedOccupancyEntity> bedOccupancies;

    private String notes; // Custom notes, not use enum


}