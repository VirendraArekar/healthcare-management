package com.hospital.billing.service;

import com.hospital.billing.repository.BillingRepository;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RefundService extends DomainServiceSupport {
  private final BillingRepository repository;

  public RefundService(
      BillingRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  public Object request(UUID invoiceId, Jwt actor) {
    repository.lockInvoice(invoiceId);
    var invoice = repository.get("invoice", invoiceId);
    Validation.require("PAID".equals(invoice.get("status")), "Only paid invoices may be refunded");
    Validation.require(repository.pendingRefunds(invoiceId) == 0, "A refund is already active");
    audit.record(actor.getSubject(), "REFUND_REQUEST", invoiceId);
    return create(
        "refund",
        Map.of(
            "invoiceId",
            invoiceId.toString(),
            "status",
            "REQUESTED",
            "requestedBy",
            actor.getSubject(),
            "amount",
            invoice.get("total")),
        null);
  }

  public Object decide(UUID id, String decision, String reason, Jwt actor) {
    repository.lockRefund(id);
    var refund = repository.get("refund", id);
    Validation.require("REQUESTED".equals(refund.get("status")), "Refund is not pending");
    Validation.require(
        !actor.getSubject().equals(refund.get("requestedBy")),
        "A different user must approve refunds");
    Validation.require(
        Set.of("APPROVED", "REJECTED").contains(decision), "Invalid refund decision");
    long version = ((Number) refund.remove("version")).longValue();
    refund.put("status", decision);
    refund.put("approvedBy", actor.getSubject());
    refund.put("reason", reason);
    audit.record(actor.getSubject(), "REFUND_" + decision, id);
    return update("refund", id, refund, version, null);
  }

  public Object settle(UUID id, Jwt actor) {
    repository.lockRefund(id);
    var prior = repository.refundSettlement(id);
    if (!prior.isEmpty()) return prior.getFirst();
    var refund = repository.get("refund", id);
    Validation.require("APPROVED".equals(refund.get("status")), "Refund must be approved first");
    UUID invoiceId = UUID.fromString(refund.get("invoiceId").toString());
    repository.lockInvoice(invoiceId);
    var invoice = repository.get("invoice", invoiceId);
    Validation.require("PAID".equals(invoice.get("status")), "Invoice is not refundable");
    BigDecimal amount = new BigDecimal(invoice.get("total").toString());
    UUID settlement = UUID.randomUUID();
    repository.insertRefundSettlement(settlement, id, invoiceId, amount);
    repository.refundInvoice(invoiceId);
    long version = ((Number) refund.remove("version")).longValue();
    refund.put("status", "COMPLETED");
    refund.put("amount", amount);
    update("refund", id, refund, version, null);
    events.publish(
        "billing.refund-completed.v1",
        invoiceId,
        Map.of("refundId", id.toString(), "patientId", invoice.get("patientId"), "amount", amount));
    audit.record(actor.getSubject(), "REFUND_SETTLED", id);
    return Map.of(
        "id",
        settlement,
        "refundId",
        id,
        "invoiceId",
        invoiceId,
        "amount",
        amount,
        "status",
        "COMPLETED",
        "provider",
        "SIMULATED");
  }
}
