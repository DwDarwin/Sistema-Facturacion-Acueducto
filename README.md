# AGUAPAL - Sistema de facturación de agua

Aplicación web en Java 21 + Spring Boot + PostgreSQL 

## Funcionalidad disponible Backend

- CRUD web de suscriptores, sin borrado físico del historial.
- CRUD web de cuentas de servicio con código automático secuencial.
- CRUD web de tarifas con vigencias y valores en pesos colombianos, COP.
- Descuentos y subsidios recurrentes.
- Descuentos y subsidios temporales con fecha inicial y final.
- Asignación de descuentos y subsidios a cuentas de servicio.
- Descuentos y subsidios ocasionales de valor libre directamente sobre una factura.
- Generación mensual idempotente de facturas.
- Cartera anterior separada del cargo del periodo actual.
- Detección de morosidad por fecha de vencimiento y saldo.
- Recaudos y abonos aplicados automáticamente a las facturas más antiguas.
- Historial de los últimos recaudos.
- Validación para evitar pagos superiores a la deuda mientras no exista módulo de saldos a favor.
- Presentación monetaria forzada a pesos colombianos.


## Próximas iteraciones

- Consulta detallada de cartera por usuario y antigüedad.
- Reporte específico de morosos.
- Reportes de facturación y recaudo.
- Auditoría de operaciones.
- Integración futura para envío por WhatsApp.
