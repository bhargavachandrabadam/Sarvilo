package com.easy.stazy.payments.controller;

import com.easy.stazy.payments.dto.request.TenantPaymentRequestDto;
import com.easy.stazy.payments.dto.response.TenantRentPaymentsDto;
import com.easy.stazy.payments.entity.TenantPaymentEntity;
import com.easy.stazy.payments.service.TenantPaymentService;
import com.easy.stazy.shared.common.util.SuccessResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = " Tenant Payment Management", description = "APIs for tenants to manage their payments")
@RestController
@RequestMapping("/v1/tenant/tenant-payments")
@RequiredArgsConstructor
public class TenantPaymentController {

    private final TenantPaymentService tenantPaymentService;

    @Operation(summary = "Mark Payment as Paid", description = "Allows tenants to mark their rent payment as paid.")
    @PostMapping
    public ResponseEntity<?> markAsPaid(@Valid @RequestBody TenantPaymentRequestDto dto) {
        tenantPaymentService.savePayment(dto);
        // TODO: Notify owner here
        return ResponseEntity.status(HttpStatus.CREATED).body("Payment marked as paid successfully.");
    }

    @Operation(summary = "Get Payments by Tenant", description = "Fetches all rent payments associated with a specific tenant.")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<SuccessResponseBody> getPaymentsByTenant(@PathVariable Long tenantId) {
        List<TenantRentPaymentsDto> payments = tenantPaymentService.getPaymentsByTenant(tenantId);
        SuccessResponseBody responseBody = new SuccessResponseBody("Payments fetched successfully", payments);
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "Get Payment by ID", description = "Fetches a specific tenant payment by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<TenantPaymentEntity> getPaymentById(@PathVariable Long id) {
        TenantPaymentEntity payment = tenantPaymentService.getPaymentById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    @Operation(summary = "Get Tenant Rent Summary", description = "Fetches a month-by-month rent summary for a tenant from joining date to current month.")
    @GetMapping("/tenant/{tenantId}/rents-summary")
    public ResponseEntity<SuccessResponseBody> getTenantRentsSummary(@PathVariable Long tenantId) {
        var summary = tenantPaymentService.getTenantRentsSummary(tenantId);
        SuccessResponseBody responseBody = new SuccessResponseBody("Tenant rent summary fetched successfully", summary);
        return ResponseEntity.ok(responseBody);
    }
}
