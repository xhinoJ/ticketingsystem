package com.traffic.eventmanager.service;

import com.traffic.eventmanager.entity.Event;
import com.traffic.eventmanager.entity.Violation;
import com.traffic.eventmanager.entity.ViolationFactory;
import com.traffic.eventmanager.entity.response.ViolationResponseDTO;
import com.traffic.eventmanager.repository.ViolationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class ViolationService {
    private final ViolationRepository violationRepository;

    /**
     * Method used to update the Violation status into payed.
     * <p>
     * There are a couple of controls, based on uuid param existence and sum bigger than fine or not
     *
     * @param violationId @{@link UUID}
     * @param sum         @{@link Double}
     * @return @{@link Violation}
     */
    public Violation payViolation(UUID violationId, Double sum) {
        Violation violation = violationRepository.getViolation(violationId);
        if (violation == null || violation.getFine() > sum) {
            throw new NotAcceptableStatusException("Wrong params");
        }
        if (violation.getPaid()) {
            throw new NotAcceptableStatusException("Wrong params");
        }

        violation.setPaid(true);
        violationRepository.updateViolation(violation);

        return violation;
    }

    /**
     * Method that calculated the total paid and unpaid violations.
     * <br/>
     * <p>
     * It loops through all the violations and does the summing
     *
     * @return
     */
    public ViolationResponseDTO getViolationSummary() {

        AtomicReference<Double> paidSum = new AtomicReference<>(0d);
        AtomicReference<Double> unpaidSum = new AtomicReference<>(0d);

        processEveryValidation(paidSum, unpaidSum);

        return ViolationResponseDTO.builder().paidViolations(paidSum.get())
                .unpaidViolations(unpaidSum.get())
                .build();
    }

    private void processEveryValidation(AtomicReference<Double> paidSum, AtomicReference<Double> unpaidSum) {
        violationRepository.findAll().forEach(violation -> {
            if (violation.getPaid()) {
                paidSum.updateAndGet(v -> v + violation.getFine());
            } else {
                unpaidSum.updateAndGet(v -> v + violation.getFine());
            }
        });
    }

    public Violation saveViolation(Violation violation) {
        violationRepository.save(violation);
        return violation;
    }

    public Collection<Violation> getAllViolations() {
        return violationRepository.findAll();
    }

    /**
     * Method check if an event is a violation or not
     * <p>
     * If it a violation, a "save" is performed
     *
     * @param event @{@link Event}
     */
    public Violation processPossibleViolationFromEvent(Event event) {
        Violation violation = ViolationFactory.createViolation(event);
        if (violation != null) {
            violation.setEventId(event.getId());
            violationRepository.save(violation);
        }
        return violation;
    }
}