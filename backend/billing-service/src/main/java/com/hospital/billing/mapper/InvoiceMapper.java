package com.hospital.billing.mapper;

import com.hospital.billing.entity.*;
import com.hospital.common.mapper.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper implements AggregateMapper<InvoiceEntity> {
  public InvoiceEntity create(Map<String, Object> data) {
    var entity = new InvoiceEntity();
    update(entity, data);
    return entity;
  }

  public void update(InvoiceEntity entity, Map<String, Object> data) {
    if (data.containsKey("patientId")) entity.setPatientId(Values.uuid(data.get("patientId")));
    if (data.get("subtotal") != null) entity.setSubtotal(Values.money(data.get("subtotal")));
    if (data.get("taxPercent") != null) entity.setTaxPercent(Values.money(data.get("taxPercent")));
    if (data.get("discount") != null) entity.setDiscount(Values.money(data.get("discount")));
    if (data.get("total") != null) entity.setTotal(Values.money(data.get("total")));
    if (data.get("status") != null) entity.setStatus(Values.text(data.get("status")));
    if (data.get("currency") != null) entity.setCurrency(Values.text(data.get("currency")));
    if (data.get("lineItems") instanceof List<?> lines) {
      entity.setLineItems(
          new ArrayList<>(
              lines.stream()
                  .map(
                      item -> {
                        var line = (Map<?, ?>) item;
                        return new InvoiceLineItem(
                            Objects.toString(line.get("description"), "Service"),
                            Integer.parseInt(line.get("quantity").toString()),
                            new BigDecimal(line.get("unitPrice").toString()));
                      })
                  .toList()));
    } else if (entity.getLineItems().isEmpty()) {
      entity.setLineItems(
          new ArrayList<>(
              List.of(new InvoiceLineItem("Hospital services", 1, entity.getSubtotal()))));
    }
  }

  public Map<String, Object> response(InvoiceEntity entity) {
    var out = new LinkedHashMap<String, Object>();
    out.put("id", entity.getId());
    out.put("version", entity.getVersion());
    out.put("createdAt", entity.getCreatedAt().toString());
    if (entity.getPatientId() != null) out.put("patientId", entity.getPatientId().toString());
    if (entity.getSubtotal() != null) out.put("subtotal", entity.getSubtotal());
    if (entity.getTaxPercent() != null) out.put("taxPercent", entity.getTaxPercent());
    if (entity.getDiscount() != null) out.put("discount", entity.getDiscount());
    if (entity.getTotal() != null) out.put("total", entity.getTotal());
    if (entity.getStatus() != null) out.put("status", entity.getStatus());
    if (entity.getCurrency() != null) out.put("currency", entity.getCurrency());
    out.put("lineItems", entity.getLineItems().stream().map(InvoiceLineItem::response).toList());
    return out;
  }
}
