package com.damidu.supermariodsa.model;

/**
 * Encapsulates gameplay events queued inside CustomQueue (DSA Requirement D).
 */
public class GameEvent {

    public enum EventType {
        COIN_COLLECTED,
        ENEMY_DEFEATED,
        POWERUP_ACQUIRED,
        PLAYER_HURT,
        UNDO_PERFORMED,
        LEVEL_COMPLETED,
        CHECKPOINT_REACHED
    }

    private final EventType type;
    private final String message;
    private final int pointDelta;
    private final long timestamp;

    public GameEvent(EventType type, String message, int pointDelta) {
        this.type = type;
        this.message = message;
        this.pointDelta = pointDelta;
        this.timestamp = System.currentTimeMillis();
    }

    public EventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getPointDelta() {
        return pointDelta;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%+d pts)", type, message, pointDelta);
    }
}
