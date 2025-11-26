package com.easy.stazy.pgmanagement.pg.service.impl;

import com.easy.stazy.pgmanagement.admin.repository.PgManagementRepository;
import com.easy.stazy.pgmanagement.admin.service.PgAuditService;
import com.easy.stazy.authentication.repository.UserRepository;
import com.easy.stazy.authentication.service.UserService;
import com.easy.stazy.pgmanagement.pg.entities.*;
import com.easy.stazy.pgmanagement.pg.enums.BedOccupancyStatus;
import com.easy.stazy.pgmanagement.pg.enums.RentalType;
import com.easy.stazy.pgmanagement.pg.repository.*;
import com.easy.stazy.pgmanagement.tenant.dto.request.TenantExcelRowDto;
import com.easy.stazy.authentication.entities.UsersEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import com.easy.stazy.pgmanagement.tenant.repository.TenantInfoRepository;
import com.easy.stazy.shared.common.exception.ResourceAlreadyExistsException;
import com.easy.stazy.shared.service.SecurityUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for importing tenant data from Excel files.
 * <p>
 * This class is responsible for parsing Excel files and handling the creation or update
 * of tenant-related entities in the system. It is kept separate from the main import service
 * to encapsulate all Excel-specific logic, such as parsing, validation, and mapping rows to entities.
 * <p>
 * By maintaining a dedicated Excel import service, the application can easily support additional
 * import formats (like CSV or JSON) in the future, and keep the codebase modular and maintainable.
 * This separation also allows for easier testing and extension of import logic without affecting
 * the core import orchestration or other import mechanisms.
 */
@Service
@RequiredArgsConstructor
public class TenantExcelImportService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final TenantInfoRepository tenantInfoRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final BedOccupancyRepository bedOccupancyRepository;
    private final PgManagementRepository pgRepository;
    private final RentalOptionRepository rentalOptionRepository;
    private final PgAuditService pgAuditService;
    private final SecurityUserService securityUserService;


    @Transactional
    public void importTenantsFromExcel(MultipartFile file, long pgId) throws Exception {
        List<TenantExcelRowDto> rows = parseExcel(file.getInputStream());
        for (TenantExcelRowDto row : rows) {
            Optional<UsersEntity> user = userRepository.findByPhoneNumber(row.getContactNumber());
            if (user.isEmpty() && row.getContactNumber() != null) {
                user = Optional.ofNullable(userService.createUser(row.getContactNumber(), "TENANT", row.getTenantName(), row.getEmail()));
            }
            BedEntity bed = getBedEntity(pgId, row);
            // Find RentalOptionEntity based on rentalType and sharingType from DTO
            RentalOptionEntity rentalOption = rentalOptionRepository.findByPgIdAndSharingTypeAndDurationType(
                    pgId,
                    row.getSharingType(),
                    RentalType.MONTHLY
            );
            if (rentalOption == null) {
                throw new RuntimeException("No rental option found for PG: " + pgId + ", sharing type: " + row.getSharingType());
            }
            TenantInfoEntity tenantInfo = createTenantInfoEntity(row, user.orElse(null), pgId);
            tenantInfoRepository.save(tenantInfo);
            createBedOccupancy(bed, tenantInfo, RentalType.MONTHLY.name(), row.getSharingType());
        }
    }

    @Transactional
    public void importTenantsAndCreateEntitiesFromExcel(MultipartFile file, long pgId) throws Exception {
        List<TenantExcelRowDto> rows = parseExcel(file.getInputStream());
        Set<String> existingUserPgSet = getExistingUserPgSet();
        for (int i = 0; i < rows.size(); i++) {
            TenantExcelRowDto row = rows.get(i);
            Optional<UsersEntity> user = userRepository.findByPhoneNumber(row.getContactNumber());
            if (user.isEmpty() && row.getContactNumber() != null) {
                user = Optional.ofNullable(userService.createUser(row.getContactNumber(), "TENANT", row.getTenantName(), row.getEmail()));
            }
            // Optimized duplicate check
            if (user.isPresent() && existingUserPgSet.contains(user.get().getId() + "_" + pgId)) {
                continue;
            }
            BedEntity bed = getOrCreateBedEntity(pgId, row);
            // Check if bed is already occupied
            if (bedOccupancyRepository.existsByBed_IdAndStatus(bed.getId(), "ACTIVE")) {
                // Skip if bed is already occupied
                throw new ResourceAlreadyExistsException("Bed " + row.getBedNo() + " in Room " + row.getRoomNo() + " on Floor " + row.getFloor() + " is already occupied.");
            }
            TenantInfoEntity tenantInfo = createTenantInfoEntity(row, user.orElse(null), pgId);
            createBedOccupancy(bed, tenantInfo, RentalType.MONTHLY.name(), row.getSharingType());
            // Add to set to prevent future duplicates in this import
            user.ifPresent(usersEntity -> existingUserPgSet.add(usersEntity.getId() + "_" + pgId));
        }
        String userId = securityUserService.getCurrentUserId();
        String userName = securityUserService.getCurrentUserName();
        if ((userId != null && !userId.isEmpty()) || (userName != null && !userName.isEmpty())) {
            pgAuditService.storeAudit(userName, userId, pgId, file);
        }
    }

    private Set<String> getExistingUserPgSet() {
        Set<String> existingUserPgSet = new HashSet<>();
        List<Object[]> existingUserPgList = tenantInfoRepository.findAllUserIdAndPgId();
        for (Object[] arr : existingUserPgList) {
            existingUserPgSet.add(arr[0] + "_" + arr[1]);
        }
        return existingUserPgSet;
    }


    private BedEntity getBedEntity(long pgId, TenantExcelRowDto row) {
        PgManagementEntity pg = pgRepository.findById(pgId)
                .orElseThrow(() -> new RuntimeException("PG not found: " + pgId));
        FloorEntity floor = floorRepository.findByFloorNumberAndPg(row.getFloor(), pg);
        if (floor == null) throw new RuntimeException("Floor not found: " + row.getFloor());
        RoomEntity room = roomRepository.findByRoomNumberAndFloor(row.getRoomNo(), floor);
        if (room == null) throw new RuntimeException("Room not found: " + row.getRoomNo());
        BedEntity bed = bedRepository.findByBedNumberAndRoom(row.getBedNo(), room);
        if (bed == null) throw new RuntimeException("Bed not found: " + row.getBedNo());
        return bed;
    }

    private BedEntity getOrCreateBedEntity(long pgId, TenantExcelRowDto row) {
        PgManagementEntity pg = pgRepository.findById(pgId)
                .orElseThrow(() -> new RuntimeException("PG not found: " + pgId));
        FloorEntity floor = floorRepository.findByFloorNumberAndPg(row.getFloor(), pg);
        if (floor == null) {
            floor = new FloorEntity();
            floor.setFloorNumber(row.getFloor());
            floor.setPg(pg);
            floor = floorRepository.save(floor);
        }
        RoomEntity room = roomRepository.findByRoomNumberAndFloor(row.getRoomNo(), floor);
        if (room == null) {
            room = new RoomEntity();
            room.setRoomNumber(row.getRoomNo());
            room.setFloor(floor);
            // Set rental option based on sharing type and rental type
            String sharingType = row.getSharingType();
            List<RentalOptionEntity> rentalOption = rentalOptionRepository.findByPgIdAndSharingTypeNative(pgId, sharingType);
            if (rentalOption.isEmpty()) {
                throw new RuntimeException("No rental option found for PG: " + pgId + ", sharing type: " + sharingType);
            }
            room.setRentalOptions(rentalOption);
            room = roomRepository.save(room);
        }
        BedEntity bed = bedRepository.findByBedNumberAndRoom(row.getBedNo(), room);
        if (bed == null) {
            bed = new BedEntity();
            bed.setBedNumber(row.getBedNo());
            bed.setRoom(room);
            bed = bedRepository.save(bed);
        }
        return bed;
    }

    private void createBedOccupancy(BedEntity bed, TenantInfoEntity tenantInfo, String rentalType, String sharingType) {
        BedOccupancyEntity bedOccupancy = new BedOccupancyEntity();
        bedOccupancy.setBed(bed);
        bedOccupancy.setTenant(tenantInfo);
        bedOccupancy.setStatus(BedOccupancyStatus.ACTIVE.name());
        bedOccupancy.setJoiningDate(LocalDate.now());
        bedOccupancy.setRentalType(rentalType);
        bedOccupancy.setSharingType(sharingType);
        bedOccupancyRepository.save(bedOccupancy);
    }

    private TenantInfoEntity createTenantInfoEntity(TenantExcelRowDto row, UsersEntity user, Long pgId) {
        TenantInfoEntity tenantInfo = new TenantInfoEntity();
        tenantInfo.setUser(user);
        PgManagementEntity pg = pgRepository.findById(pgId)
                .orElseThrow(() -> new RuntimeException("PG not found: " + pgId));
        tenantInfo.setPg(pg);
        tenantInfo.setNotes(row.getNotes());
        tenantInfoRepository.save(tenantInfo);
        return tenantInfo;
    }

    private List<TenantExcelRowDto> parseExcel(InputStream is) throws Exception {
        List<TenantExcelRowDto> rows = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        if (iterator.hasNext()) iterator.next();// skip header
        iterator.next(); // skip second row
        while (iterator.hasNext()) {
            Row row = iterator.next();
            // Check if entire row is null or empty
            boolean isEmpty = true;
            for (int i = 0; i <= 8; i++) {
                if (!isCellEmpty(row, i)) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {
                continue;
            }
            TenantExcelRowDto dto = new TenantExcelRowDto();
            dto.setTenantName(getCellValue(row, 0));
            dto.setContactNumber(getCellValue(row, 1));
            dto.setEmail(getCellValue(row, 2));
            dto.setFloor(getCellValue(row, 3));
            dto.setRoomNo(getCellValue(row, 4));
            dto.setSharingType(getCellValue(row, 5));
            dto.setBedNo(getCellValue(row, 6));
            dto.setIdProofType(getCellValue(row, 7));
            dto.setNotes(getCellValue(row, 8));
            rows.add(dto);
        }
        workbook.close();
        return rows;
    }

    private String getCellValue(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double d = cell.getNumericCellValue();
                if (d == (long) d) {
                    return String.valueOf((long) d);
                } else {
                    return String.valueOf(d);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

    /**
     * Checks if a cell in the given row is empty (null or blank after trimming).
     *
     * @param row     The Excel row.
     * @param cellNum The cell index.
     * @return true if the cell is empty or blank, false otherwise.
     */
    private boolean isCellEmpty(Row row, int cellNum) {
        String val = getCellValue(row, cellNum);
        return val == null || val.trim().isEmpty();
    }
}
