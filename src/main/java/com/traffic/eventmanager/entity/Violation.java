package com.traffic.eventmanager.entity;

import lombok.ToString;

import java.util.UUID;


@ToString
public abstract class Violation {
    private final UUID id = UUID.randomUUID();
    private UUID eventId;
    private boolean paid;

    protected Violation(UUID eventId) {
        this.eventId = eventId;
    }

    public abstract Double getFine();

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public boolean getPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }


}
