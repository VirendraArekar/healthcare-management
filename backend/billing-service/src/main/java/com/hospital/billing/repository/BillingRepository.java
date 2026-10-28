package com.hospital.billing.repository;

import com.hospital.common.repository.ResourceOperations;

public interface BillingRepository extends ResourceOperations {
  java.util.List<java.util.Map<String, Object>> lockInvoice(java.util.UUID id);

  java.util.List<java.util.Map<String, Object>> findPaymentByIdempotencyKey(java.util.UUID key);

  int insertPayment(
      java.util.UUID id, java.util.UUID invoice, java.util.UUID key, java.math.BigDecimal amount);

  int markInvoicePaid(java.util.UUID id);

  long pendingRefunds(java.util.UUID id);

  Object payments();

  Object balances();

  void refundInvoice(java.util.UUID id);

  Object findPatientRecords(String owner, int page);

  void lockRefund(java.util.UUID id);

  java.util.List<java.util.Map<String, Object>> refundSettlement(java.util.UUID id);

  void insertRefundSettlement(
      java.util.UUID settlement,
      java.util.UUID refund,
      java.util.UUID invoice,
      java.math.BigDecimal amount);
}
