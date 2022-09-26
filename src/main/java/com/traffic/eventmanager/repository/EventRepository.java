package com.traffic.eventmanager.repository;

import com.traffic.eventmanager.entity.Event;
import lombok.Synchronized;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EventRepository {
    private final Map<UUID, Event> storage = new ConcurrentHashMap<>();

    @Synchronized
    public void save(Event event) {
        storage.putIfAbsent(event.getId(), event);
    }

    public Event getEvent(UUID id) {
        return storage.get(id);
    }

    public Collection<Event> findAll() {
        return storage.values();
    }

    public void deleteAllEvents() {
        storage.clear();
    }
}
