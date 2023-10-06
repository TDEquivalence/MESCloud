CREATE OR REPLACE VIEW composed_summary AS
SELECT DISTINCT
	cpo.id, cpo.created_at, cpo.code,
	s.amount, s.reliability,
	po.input_batch, po.source, po.gauge, po.category, po.washing_process,
	b.id AS batch_id, b.code AS batch_code, b.is_approved AS is_batch_approved
FROM composed_production_order cpo
LEFT JOIN sample s ON cpo.id = s.composed_production_order_id
LEFT JOIN hit h ON s.id = h.sample_id
LEFT JOIN production_order po ON cpo.id = po.composed_production_order_id
LEFT JOIN batch b ON cpo.id = b.composed_production_order_id;