package com.easy.stazy.pgmanagement.pg.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * Service for generating and downloading an Excel template for tenant data import.
 * <p>
 * This service uses Apache POI to create an Excel (.xlsx) file with headers:
 * Tenant Name, Phone, Room No, Bed No, Status, Notes. The template is styled
 * with bold headers and minimum column widths for better readability.
 * <p>
 * Usage: Call {@link #downloadTenantExcelTemplate(HttpServletResponse)} to stream
 * the template as a file download to the client.
 */
@Service
public class TenantExcelTemplateService{
    /**
     * Generates and streams an Excel template for tenant data import.
     *
     * @param response the HTTP response to write the Excel file to
     * @throws IOException if an I/O error occurs during writing
     */
    public void downloadTenantExcelTemplate(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tenants");
        CellStyle headerCellStyle = createHeaderCellStyle(workbook);
        createHeaderRowWithStyle(sheet, headerCellStyle);
        writeWorkbookToResponse(response, workbook);
    }

    /**
     * Creates the header row with bold style and sets minimum column widths.
     *
     * @param sheet the Excel sheet to modify
     * @param headerCellStyle the style to apply to header cells
     */
    private static void createHeaderRowWithStyle(Sheet sheet, CellStyle headerCellStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Tenant Name", "Contact Number(+91)", "Email", "Floor","Room No", "Bed No","Sharing Type", "ID Proof Type","Notes"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }
        sheet.createRow(1);
        int minWidth = 20 * 256; // 20 characters wide
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            }
        }
    }

    /**
     * Creates a bold cell style for the header row.
     *
     * @param workbook the workbook to create the style in
     * @return the created CellStyle
     */
    private static CellStyle createHeaderCellStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
        headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerCellStyle.setBorderRight(BorderStyle.MEDIUM);
        return headerCellStyle;
    }

    /**
     * Writes the workbook to the HTTP response and closes it.
     *
     * @param response the HTTP response
     * @param workbook the workbook to write
     * @throws IOException if an I/O error occurs
     */
    private static void writeWorkbookToResponse(HttpServletResponse response, Workbook workbook) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=tenant-template.xlsx");
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
        workbook.close();
    }
}
