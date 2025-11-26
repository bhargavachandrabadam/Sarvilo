package com.easy.stazy.pgmanagement.pg.service;

import com.easy.stazy.pgmanagement.pg.dto.response.HostelDetailsResponseDto;

import org.springframework.stereotype.Service;


/**
 * Service interface for managing PG hierarchy (PG -> Floors -> Rooms -> Beds)
 * and exposing operations to create elements and query hierarchical/occupancy data.
 */
@Service
public interface HostelDetailsService {

     HostelDetailsResponseDto getHostelDetails(long hostelId);
}
