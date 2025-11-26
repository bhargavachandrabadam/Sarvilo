package com.easy.stazy.pgmanagement.owner.service;

import com.easy.stazy.pgmanagement.pg.dto.response.HelpItemDto;
import java.util.List;

public interface OwnerHelpService {
    List<HelpItemDto> getAllHelpItems();
}

