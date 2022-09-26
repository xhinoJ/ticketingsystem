package com.traffic.eventmanager.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestDTO {
    private LocalDateTime eventDate;
    private String eventType;
    private String licensePlate;
    @NotNull(message = "Speed cannot be null")
    @Min(0)
    private Double speed;
    @NotNull(message = "Speed limit cannot be null")
    @Min(0)
    private Double limit;
    private String unit;
}
