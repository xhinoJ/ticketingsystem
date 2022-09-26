package com.traffic.eventmanager.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ViolationResponseDTO {

    private Double paidViolations;
    private Double unpaidViolations;

}
