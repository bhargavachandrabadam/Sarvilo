package com.easy.stazy.payments.repository;

import com.easy.stazy.payments.enums.PaymentStatus;
import com.easy.stazy.payments.entity.TenantPaymentEntity;
import com.easy.stazy.pgmanagement.tenant.entities.TenantInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantPaymentRepository extends JpaRepository<TenantPaymentEntity, Long> {
    List<TenantPaymentEntity> findByTenantIdOrderByYearDescMonthDesc(Long tenantId);

    boolean existsByTenantAndMonthAndYearAndStatus(TenantInfoEntity tenant, Integer month, Integer year, PaymentStatus status);

    // Bulk fetch: all payments for tenants for given month/year with status
    List<TenantPaymentEntity> findByTenant_IdInAndMonthAndYearAndStatus(List<Long> tenantIds, Integer month, Integer year, PaymentStatus status);

    List<TenantPaymentEntity> findByTenantIdInAndMonthAndYear(List<Long> occupiedTenantIds, int monthValue, int year);

    List<TenantPaymentEntity> findByMonthAndYearAndStatus(int month, int year, PaymentStatus paymentStatus);
}
