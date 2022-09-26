package com.traffic.eventmanager.entity;

import java.util.UUID;

public class SpeedViolation extends Violation {

    public SpeedViolation(UUID id) {
        super(id);
    }

    @Override
    public Double getFine() {
        return 50d;
    }
}
