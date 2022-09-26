package com.traffic.eventmanager.entity;


import com.traffic.eventmanager.entity.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    private UUID id;
    private LocalDateTime eventDate;
    private EventType eventType;
    private String licensePlate;

    private Double speed;
    @NotNull(message = "Speed limit Name cannot be null")
    @Min(0)
    private Double limit;
    private String unit;
    private Boolean processed;

}
