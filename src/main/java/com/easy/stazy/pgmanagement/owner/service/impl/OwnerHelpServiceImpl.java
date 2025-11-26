package com.easy.stazy.pgmanagement.owner.service.impl;

import com.easy.stazy.pgmanagement.owner.config.HelpItemsConfig;
import com.easy.stazy.pgmanagement.pg.dto.response.HelpItemDto;
import com.easy.stazy.pgmanagement.owner.service.OwnerHelpService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OwnerHelpServiceImpl implements OwnerHelpService {
    @Override
    public List<HelpItemDto> getAllHelpItems() {
        return HelpItemsConfig.HELP_ITEMS;
    }
}

