package vn.iotstar.coolenglish.audit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;

public final class AuditSnapshotBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AuditSnapshotBuilder() {
    }

    public static String buildDetails(Object entity) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("entityType", entity.getClass().getSimpleName());
        payload.put("entityId", resolveEntityId(entity));
        payload.put("state", collectScalarFields(entity));
        return toJson(payload);
    }

    public static String resolveEntityId(Object entity) {
        Class<?> current = entity.getClass();
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        return value != null ? String.valueOf(value) : null;
                    } catch (IllegalAccessException ignored) {
                        return null;
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private static Map<String, Object> collectScalarFields(Object entity) {
        Map<String, Object> state = new LinkedHashMap<>();
        Class<?> current = entity.getClass();
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                if (field.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if ("password".equalsIgnoreCase(field.getName())) {
                    continue;
                }

                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    if (isScalarValue(value)) {
                        state.put(field.getName(), normalizeValue(value));
                    }
                } catch (IllegalAccessException ignored) {
                    // Skip fields that cannot be read.
                }
            }
            current = current.getSuperclass();
        }
        return state;
    }

    private static boolean isScalarValue(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Enum<?>
                || value instanceof TemporalAccessor
                || value instanceof UUID
                || value instanceof Character;
    }

    private static Object normalizeValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Enum<?> enumValue) {
            return enumValue.name();
        }
        if (value instanceof TemporalAccessor || value instanceof UUID || value instanceof Character) {
            return String.valueOf(value);
        }
        return value;
    }

    private static String toJson(Map<String, Object> payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            return payload.toString();
        }
    }
}
