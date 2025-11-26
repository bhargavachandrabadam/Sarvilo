package com.easy.stazy.pgmanagement.owner.controller;

import com.easy.stazy.pgmanagement.pg.dto.response.HelpItemDto;
import com.easy.stazy.pgmanagement.owner.service.OwnerHelpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Owner Help", description = "APIs for hostel owners to access help items")
@RestController
@RequestMapping("/v1/owner/help")
@RequiredArgsConstructor
public class OwnerHelpController {
    private final OwnerHelpService ownerHelpService;

    @Operation(summary = "Get All Help Items", description = "Fetches all help items available for hostel owners.")
    @GetMapping
    public ResponseEntity<List<HelpItemDto>> getAllHelpItems() {
        return ResponseEntity.ok(ownerHelpService.getAllHelpItems());
    }


}
