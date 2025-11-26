package com.easy.stazy.pgmanagement.pg.dto.excel;

import com.poiji.annotation.ExcelCellName;
import lombok.Data;

@Data
public class TenantExcelTemplateDto {

    @ExcelCellName("Tenant Name")
    private String tenantName;

    @ExcelCellName("Mobile Number")
    private String phoneNumber;

    @ExcelCellName("Room No")
    private String roomNo;

    @ExcelCellName("Bed No")
    private String bedNo;

    @ExcelCellName("Status")
    private String status;

    @ExcelCellName("Notes")
    private String notes;
}
