package com.traffic.eventmanager.repository;

import com.traffic.eventmanager.entity.Violation;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ViolationRepository {
    private final Map<UUID, Violation> violationMap = new ConcurrentHashMap<>();

    public void save(Violation violation) {
        violationMap.putIfAbsent(violation.getId(), violation);
    }

    public Violation getViolation(UUID id) {
        return violationMap.get(id);
    }

    public Collection<Violation> findAll() {
        return violationMap.values();
    }

    public synchronized Violation updateViolation(Violation violation) {
        violationMap.replace(violation.getId(), violation);
        return violation;
    }

    public void deleteAllViolations() {
        violationMap.clear();
    }
}
