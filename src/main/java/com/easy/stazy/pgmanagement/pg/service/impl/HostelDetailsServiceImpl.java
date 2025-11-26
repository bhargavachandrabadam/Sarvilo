package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.pg.dto.response.HostelDetailsResponseDto;
import com.easy.stazy.pgmanagement.pg.mapper.responses.HostelDetailsResponseMapper;
import com.easy.stazy.pgmanagement.pg.service.HostelDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for managing PG hierarchy (PG -> Floors -> Rooms -> Beds)
 * and exposing operations to create elements and query hierarchical/occupancy data.
 */
@RequiredArgsConstructor
@Service
public class HostelDetailsServiceImpl implements HostelDetailsService {

    private final PgManagementRepository hostelRepository;
    private final HostelDetailsResponseMapper hostelDetailsResponseMapper;


    /**
     * Retrieves the details of a hostel by its ID.
     * Delegates mapping to {@code HostelDetailsResponseMapper}.
     *
     * @param hostelId the unique identifier of the hostel
     * @return a HostelDetailsResponseDto containing hostel details or null if not found
     */
    @Transactional
    @Override
    public HostelDetailsResponseDto getHostelDetails(long hostelId) {
        Optional<PgManagementEntity> hostelEntity = hostelRepository.findById(hostelId);
        return hostelEntity.map(hostelDetailsResponseMapper::hostelDetailsResponseMapper).orElse(null);
    }
}
