package com.easy.stazy.pgmanagement.pg.service;


import com.easy.stazy.pgmanagement.pg.dto.request.RoomRequestDto;

public interface RoomService {

     void createRoom(RoomRequestDto request, long floorId);
}

