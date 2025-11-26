package com.easy.stazy.payments.controller;

import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.payments.entity.TenantPaymentEntity;
import com.easy.stazy.payments.service.TenantPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Owner Payment Management", description = "APIs for owners to manage tenant payments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owner/payments")
public class OwnerPaymentController {

    private final TenantPaymentService tenantPaymentService;

    @Operation(summary = "Update Tenant Payment Status", description = "Allows owners to update the status of a tenant's payment.")
    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<TenantPaymentEntity> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus statusUpdateDto) {
        TenantPaymentEntity updatedPayment = tenantPaymentService.updatePaymentStatus(paymentId, statusUpdateDto);
        // TODO: Notify tenant here
        return ResponseEntity.ok(updatedPayment);
    }

    @Operation(summary = "Mark Tenant Payment as Paid by Owner", description = "Allows owners to mark a tenant's payment as paid and set amount paid to total.")
    @PatchMapping("/mark-paid")
    public ResponseEntity<?> markPaymentAsPaidByOwner(
            @RequestParam Long tenantId,
            @RequestParam int month,
            @RequestParam int year) {
        tenantPaymentService.markPaymentAsPaidByOwner(tenantId, month, year);
        return ResponseEntity.status(HttpStatus.OK).body("Payment marked as paid successfully.");
    }
}
