package com.easy.stazy.pgmanagement.admin.service.handler;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.shared.common.util.DateTimeUtil;
import com.easy.stazy.shared.common.util.PhoneUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import org.springframework.stereotype.Service;

@Service
public class PgSimpleFieldsHandler implements PgFieldHandler {
    @Override
    public void update(PgManagementRequestDto dto, PgManagementEntity entity) {
        if (dto.getPgName() != null) entity.setPgName(dto.getPgName().trim());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation().trim());
        if (dto.getMobileNumber() != null) {
            try {
                entity.setMobileNumber(PhoneUtil.normalizeToE164(dto.getMobileNumber().trim(), "IN"));
            } catch (NumberParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus().trim());
        if (dto.getDate() != null) entity.setDate(DateTimeUtil.parseDateString(dto.getDate()));
        if (dto.getOwnerName() != null || dto.getOwnerContactNumber() != null || dto.getOwnerEmailAddress() != null) {
            PgOwnerEntity owner = entity.getOwner();
            if (owner == null) {
                owner = new PgOwnerEntity();
            }
            if (dto.getOwnerName() != null) owner.setOwnerName(dto.getOwnerName().trim());
            if (dto.getOwnerContactNumber() != null) {
                try {
                    owner.setOwnerContactNumber(PhoneUtil.normalizeToE164(dto.getOwnerContactNumber().trim(), "IN"));
                } catch (NumberParseException e) {
                    throw new RuntimeException(e);
                }
            }
            if (dto.getOwnerEmailAddress() != null) owner.setOwnerEmailAddress(dto.getOwnerEmailAddress().trim());
            entity.setOwner(owner);
        }
        if (dto.getIsOwnerManager() != null) entity.setIsOwnerManager(dto.getIsOwnerManager());
        if (dto.getManagerName() != null) entity.setManagerName(dto.getManagerName().trim());
        if (dto.getManagerContactNumber() != null) {
            try {
                entity.setManagerContactNumber(PhoneUtil.normalizeToE164(dto.getManagerContactNumber().trim(), "IN"));
            } catch (NumberParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (dto.getAdditionalContactNumber() != null) {
            try {
                entity.setAdditionalContactNumber(PhoneUtil.normalizeToE164(dto.getAdditionalContactNumber().trim(), "IN"));
            } catch (NumberParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (dto.getState() != null) entity.setState(dto.getState().trim());
        if (dto.getDistrict() != null) entity.setDistrict(dto.getDistrict().trim());
        if (dto.getPinCode() != null) entity.setPinCode(dto.getPinCode().trim());
        if (dto.getGenderChoice() != null) entity.setGenderChoice(dto.getGenderChoice().trim());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription().trim());
        if (dto.getDailyRentalActive() != null) entity.setDailyRentalActive(dto.getDailyRentalActive());
        if (dto.getMonthlyRentalActive() != null) entity.setMonthlyRentalActive(dto.getMonthlyRentalActive());
        if (dto.getGstin() != null) entity.setGstin(dto.getGstin());
        if (dto.getUpiAddress() != null) entity.setUpiAddress(dto.getUpiAddress());
        if (dto.getBankAccountNumber() != null) entity.setBankAccountNumber(dto.getBankAccountNumber());
        if (dto.getAccountHolderName() != null) entity.setAccountHolderName(dto.getAccountHolderName());
        if (dto.getIfsc() != null) entity.setIfsc(dto.getIfsc());
    }

    @Override
    public void handle(PgManagementEntity entity, PgDetailsResponseDto dto) {
        dto.setPgName(entity.getPgName());
        dto.setLocation(entity.getLocation());
        dto.setMobileNumber(entity.getMobileNumber());
        dto.setDate(entity.getDate());
        dto.setStatus(entity.getStatus());
        if (entity.getOwner() != null) {
            dto.setOwnerName(entity.getOwner().getOwnerName());
            dto.setOwnerContactNumber(entity.getOwner().getOwnerContactNumber());
            dto.setOwnerEmailAddress(entity.getOwner().getOwnerEmailAddress());
        }
        dto.setIsOwnerManager(entity.getIsOwnerManager());
        dto.setManagerName(entity.getManagerName());
        dto.setManagerContactNumber(entity.getManagerContactNumber());
        dto.setAdditionalContactNumber(entity.getAdditionalContactNumber());
        dto.setState(entity.getState());
        dto.setDistrict(entity.getDistrict());
        dto.setPinCode(entity.getPinCode());
        dto.setGenderChoice(entity.getGenderChoice());
        dto.setDescription(entity.getDescription());
        dto.setDailyRentalActive(entity.getDailyRentalActive());
        dto.setMonthlyRentalActive(entity.getMonthlyRentalActive());
    }
}
