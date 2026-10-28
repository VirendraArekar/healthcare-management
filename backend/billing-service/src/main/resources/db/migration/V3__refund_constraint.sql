CREATE UNIQUE INDEX one_refund_per_invoice ON records((data->>'invoiceId')) WHERE kind='refund';
