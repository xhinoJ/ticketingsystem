package com.traffic.eventmanager.entity;

import com.traffic.eventmanager.entity.enums.EventType;

public class ViolationFactory {
    private ViolationFactory() {

    }

    public static Violation createViolation(Event event) {
        if (event.getEventType() == EventType.SPEED && event.getSpeed() > event.getLimit()) {
            return new SpeedViolation(event.getId());
        } else if (event.getEventType() == EventType.TURN) {
            return new RedLightViolation(event.getId());
        } else return null;

    }
}
