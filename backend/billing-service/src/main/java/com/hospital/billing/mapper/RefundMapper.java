package com.hospital.billing.mapper;

import com.hospital.billing.entity.*;
import com.hospital.common.mapper.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class RefundMapper implements AggregateMapper<RefundEntity> {
  public RefundEntity create(Map<String, Object> data) {
    var entity = new RefundEntity();
    update(entity, data);
    return entity;
  }

  public void update(RefundEntity entity, Map<String, Object> data) {
    if (data.containsKey("invoiceId")) entity.setInvoiceId(Values.uuid(data.get("invoiceId")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
    if (data.containsKey("requestedBy"))
      entity.setRequestedBy(Values.uuid(data.get("requestedBy")));
    if (data.containsKey("approvedBy")) entity.setApprovedBy(Values.uuid(data.get("approvedBy")));
    if (data.containsKey("reason")) entity.setReason(Values.text(data.get("reason")));
    if (data.get("amount") != null) entity.setAmount(Values.money(data.get("amount")));
  }

  public Map<String, Object> response(RefundEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getInvoiceId() != null) out.put("invoiceId", entity.getInvoiceId().toString());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    if (entity.getRequestedBy() != null) out.put("requestedBy", entity.getRequestedBy().toString());
    if (entity.getApprovedBy() != null) out.put("approvedBy", entity.getApprovedBy().toString());
    if (entity.getReason() != null) out.put("reason", entity.getReason());
    if (entity.getAmount() != null) out.put("amount", entity.getAmount());
    return out;
  }
}
