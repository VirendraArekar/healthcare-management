package com.hospital.billing.service;

import com.hospital.billing.repository.BillingRepository;
import com.hospital.billing.util.BillingMath;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.*;
import com.hospital.common.util.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BillingService extends DomainServiceSupport {
  private final BillingRepository repository;
  @org.springframework.beans.factory.annotation.Autowired private RefundService refundService;

  public BillingService(
      BillingRepository repository, EventPublisher events, AuditService audit, JsonCodec codec) {
    super(repository, events, audit, codec);
    this.repository = repository;
  }

  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF','PATIENT')")
  public ResponseEntity<byte[]> pdf(UUID id, Jwt jwt) {
    var r = repository.get("invoice", id);
    if (jwt.getClaimAsStringList("roles").contains("PATIENT"))
      Validation.require(
          Objects.equals(jwt.getClaimAsString("patientId"), r.get("patientId")),
          "Invoice inaccessible");
    var lines = r.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).toList();
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf")
        .body(com.hospital.common.util.SimplePdf.render("Hospital invoice - SIMULATED", lines));
  }

  @PreAuthorize("hasRole('BILLING_STAFF')")
  @Transactional
  public Object refund(UUID id, Jwt jwt) {
    return refundService.request(id, jwt);
  }

  @PreAuthorize("hasAnyRole('ADMIN','BILLING_STAFF')")
  public Object list(int page) {
    return repository.list("invoice", page, 20, "", "");
  }

  @PreAuthorize("hasRole('BILLING_STAFF')")
  public Object invoice(Map<String, Object> b) {
    directory.patient(UUID.fromString(Validation.required(b, "patientId")));
    if (b.get("lineItems") instanceof List<?> lines) {
      BigDecimal sum = BigDecimal.ZERO;
      Validation.require(!lines.isEmpty(), "At least one line item required");
      for (Object value : lines) {
        var line = (Map<?, ?>) value;
        BigDecimal price = new BigDecimal(line.get("unitPrice").toString());
        int quantity = Integer.parseInt(line.get("quantity").toString());
        Validation.require(
            price.signum() >= 0
                && price.precision() <= 14
                && price.scale() <= 2
                && quantity > 0
                && quantity <= 100000
                && line.get("description") != null,
            "Invalid line item");
        sum = sum.add(price.multiply(BigDecimal.valueOf(quantity)));
      }
      b.put("subtotal", sum);
    }

    BigDecimal subtotal = new BigDecimal(Validation.required(b, "subtotal")),
        tax = new BigDecimal(Validation.required(b, "taxPercent")),
        discount = new BigDecimal(Validation.required(b, "discount"));
    Validation.require(tax.compareTo(new BigDecimal("100")) <= 0, "Tax percentage exceeds 100");
    b.put("total", BillingMath.total(subtotal, tax, discount));
    b.put("status", "UNPAID");
    return create("invoice", b, "billing.invoice-created.v1");
  }

  @PreAuthorize("hasRole('BILLING_STAFF')")
  @Transactional
  public Object pay(UUID id, UUID key) {
    var locked = repository.lockInvoice(id);
    Validation.require(!locked.isEmpty(), "Invoice not found");
    var previous = repository.findPaymentByIdempotencyKey(key);
    if (!previous.isEmpty()) {
      Validation.require(
          previous.getFirst().get("invoice_id").equals(id),
          "Idempotency key belongs to another invoice");
      return previous.getFirst();
    }
    var invoice = repository.dto(locked.getFirst());
    Validation.require(!"PAID".equals(invoice.get("status")), "Already paid");
    UUID payment = UUID.randomUUID();
    repository.insertPayment(payment, id, key, new BigDecimal(invoice.get("total").toString()));
    repository.markInvoicePaid(id);
    events.publish(
        "billing.payment-completed.v1",
        id,
        Map.of(
            "invoiceId",
            id.toString(),
            "paymentId",
            payment.toString(),
            "amount",
            invoice.get("total"),
            "patientId",
            invoice.get("patientId")));
    return Map.of("id", payment, "status", "COMPLETED", "provider", "SIMULATED");
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Object approveRefund(UUID id, Jwt jwt) {
    return refundService.decide(id, "APPROVED", "Administrative approval", jwt);
  }

  public Object refunds(int page) {
    return repository.list("refund", page, 20, "", "");
  }

  public Object payments() {
    return repository.payments();
  }

  public Object balances() {
    return repository.balances();
  }
}
