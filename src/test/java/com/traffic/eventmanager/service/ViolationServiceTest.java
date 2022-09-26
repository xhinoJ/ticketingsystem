package com.traffic.eventmanager.service;


import com.traffic.eventmanager.entity.Event;
import com.traffic.eventmanager.entity.RedLightViolation;
import com.traffic.eventmanager.entity.SpeedViolation;
import com.traffic.eventmanager.entity.Violation;
import com.traffic.eventmanager.entity.enums.EventType;
import com.traffic.eventmanager.repository.ViolationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.NotAcceptableStatusException;

import java.net.BindException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class ViolationServiceTest {
    private ViolationService underTest;
    @Mock
    private ViolationRepository violationRepository;

    private UUID violationId;

    @BeforeEach
    void setUp() {
        underTest = new ViolationService(violationRepository);
        violationId = UUID.randomUUID();
    }

    @Test
    void payViolation_withSumLowerThanFine() {
        Violation violation = new SpeedViolation(violationId);
        Double sum = violation.getFine() - 2;
        given(violationRepository.getViolation(any(UUID.class))).willReturn(violation);
        assertThrows(NotAcceptableStatusException.class, () -> underTest.payViolation(violationId, sum));

    }


    @Test
    void payViolation_withPaidFine() {

        Violation violation = new SpeedViolation(violationId);
        violation.setPaid(true);
        Double sum = violation.getFine() + 2;
        given(violationRepository.getViolation(any(UUID.class))).willReturn(violation);
        assertThrows(NotAcceptableStatusException.class, () -> {
            underTest.payViolation(violationId, sum);
        });

    }

    @Test
    void payViolation_withCorrectParams() throws BindException {

        Violation violation = new SpeedViolation(violationId);
        given(violationRepository.getViolation(any(UUID.class))).willReturn(violation);
        assertTrue(underTest.payViolation(violationId, violation.getFine() + 2).getPaid());
        assertTrue(violation.getPaid());
    }

    @Test
    void getValidationSummary() {

        Violation paidViolation = new SpeedViolation(violationId);
        paidViolation.setPaid(true);
        Violation unpaidViolation = new RedLightViolation(UUID.randomUUID());
        Collection<Violation> violations = new ArrayList<>();
        violations.add(paidViolation);
        violations.add(unpaidViolation);


        given(violationRepository.findAll()).willReturn(violations);
        assertEquals(underTest.getAllViolations(), violations);
        assertEquals(underTest.getViolationSummary().getPaidViolations(),
                paidViolation.getFine());
        assertEquals(underTest.getViolationSummary().getUnpaidViolations(),
                unpaidViolation.getFine());

    }

    @Test
    void addViolation() {

        Violation violation = new SpeedViolation(violationId);
        underTest.saveViolation(violation);
        assertEquals(underTest.saveViolation(violation), violation);

    }

    @Test
    void processPossibleViolationFromEvent_WithSpeedSameAsLimit() {
        Event event = new Event();
        event.setEventType(EventType.SPEED);
        event.setSpeed(20d);
        event.setLimit(20d);

        Violation violation = underTest.processPossibleViolationFromEvent(event);
        assertNull(violation);
    }

    @Test
    void processPossibleViolationFromEvent_WithSpeedGreaterThanLimit() {
        Event event = new Event();
        event.setEventType(EventType.SPEED);
        event.setSpeed(20d);
        event.setLimit(10d);

        Violation violation = underTest.processPossibleViolationFromEvent(event);
        assertNotNull(violation);
    }

    @Test
    void processPossibleViolationFromEvent_WithSpeedLessThanLimitAndTurn() {
        Event event = new Event();
        event.setEventType(EventType.TURN);
        event.setSpeed(20d);
        event.setLimit(30d);

        Violation violation = underTest.processPossibleViolationFromEvent(event);
        assertNotNull(violation);
    }
}
