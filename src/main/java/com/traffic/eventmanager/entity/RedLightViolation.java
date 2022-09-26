package com.traffic.eventmanager.entity;

import java.util.UUID;

public class RedLightViolation extends Violation {

    public RedLightViolation(UUID id) {
        super(id);
    }

    @Override
    public Double getFine() {
        return 100d;
    }
}
