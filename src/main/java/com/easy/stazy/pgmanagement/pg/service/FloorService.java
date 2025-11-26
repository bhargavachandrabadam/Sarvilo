package com.easy.stazy.pgmanagement.pg.service;


import com.easy.stazy.pgmanagement.pg.dto.request.FloorRequestDto;

public interface FloorService {

     void createFloor(FloorRequestDto request, long pgId);
}
