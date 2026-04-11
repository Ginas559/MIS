package vn.iotstar.coolenglish.dao.impl;

import vn.iotstar.coolenglish.entity.AuditLog;

public class AuditLogDAO extends AbstractDAO<AuditLog> {

    @Override
    protected void validateEntity(AuditLog entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Audit log entity is required.");
        }
    }
}
