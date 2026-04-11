package vn.iotstar.coolenglish.audit.context;

import vn.iotstar.coolenglish.entity.UserAccount;

public final class AuditActorContext {

    private static final ThreadLocal<AuditActor> CURRENT_ACTOR = new ThreadLocal<>();

    private AuditActorContext() {
    }

    public static void setCurrentUser(UserAccount user) {
        if (user == null) {
            CURRENT_ACTOR.remove();
            return;
        }

        String role = user.getRole() != null ? user.getRole().name() : null;
        CURRENT_ACTOR.set(new AuditActor(user.getUserID(), user.getUsername(), role));
    }

    public static AuditActor getCurrentActor() {
        return CURRENT_ACTOR.get();
    }

    public static void clear() {
        CURRENT_ACTOR.remove();
    }
}
