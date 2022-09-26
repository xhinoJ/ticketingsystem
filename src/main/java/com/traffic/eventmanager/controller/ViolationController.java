package com.traffic.eventmanager.controller;

import com.traffic.eventmanager.aop.LogExecutionTime;
import com.traffic.eventmanager.entity.Violation;
import com.traffic.eventmanager.entity.response.ViolationResponseDTO;
import com.traffic.eventmanager.repository.ViolationRepository;
import com.traffic.eventmanager.service.ViolationService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@AllArgsConstructor
public class ViolationController {

    private ViolationRepository violationRepository;

    private ViolationService violationService;

    @ApiOperation(value = "Rest method used to receive the list of all the violations")
    @GetMapping(path = "/violation")
    @LogExecutionTime
    public CompletableFuture<Collection<Violation>> getAllViolations() {

        Collection<Violation> violationList = violationRepository.findAll();
        return CompletableFuture.supplyAsync(() -> violationList);

    }

    @ApiOperation(value = "Rest method used to simulate the payment of a violation." +
            "It may throw an exception when the violation is already paid or sum is smaller than fine")
    @PutMapping(path = "/violation")
    @LogExecutionTime
    public CompletableFuture<Violation> payViolation(@RequestParam("violationid") @NotNull UUID violationId,
                                                     @RequestParam("sum")
                                                     @NotNull(message = "Sum should be bigger than 0 and bigger or equal than limit")
                                                     @Min(0) Double sum) {

        Violation violation = violationService.payViolation(violationId, sum);

        return CompletableFuture.supplyAsync(() -> violation);
    }

    @ApiOperation(value = "Rest method designed for receiving total number of paid violations " +
            "and unpaid violations")
    @GetMapping(path = "/violation/summary")
    @LogExecutionTime
    public CompletableFuture<ViolationResponseDTO> getViolationSummary() {

        return CompletableFuture.supplyAsync(() -> violationService.getViolationSummary());
    }

}
