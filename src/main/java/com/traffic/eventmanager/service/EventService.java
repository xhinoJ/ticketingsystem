package com.traffic.eventmanager.service;

import com.traffic.eventmanager.entity.Event;
import com.traffic.eventmanager.entity.enums.EventType;
import com.traffic.eventmanager.entity.request.EventRequestDTO;
import com.traffic.eventmanager.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.traffic.eventmanager.EnumUtilFinder.findEnumInsensitiveCase;

@Service
@Slf4j
@AllArgsConstructor
public class EventService {
    private EventRepository eventRepository;

    /**
     * Method used to transform the request dto into entity and persist on memory
     *
     * @param eventRequest @{@link EventRequestDTO}
     * @return @{@link Event}
     */
    public Event saveEvent(EventRequestDTO eventRequest) {
        Event event = buildEventFromRequest(eventRequest);
        eventRepository.save(event);

        return event;
    }

    private Event buildEventFromRequest(EventRequestDTO eventRequest) {
        return Event.builder()
                .id(UUID.randomUUID())
                .eventType(findEnumInsensitiveCase(EventType.class, eventRequest.getEventType()))
                .eventDate(eventRequest.getEventDate())
                .licensePlate(eventRequest.getLicensePlate())
                .limit(eventRequest.getLimit())
                .processed(false)
                .speed(eventRequest.getSpeed())
                .unit(eventRequest.getUnit())
                .build();
    }
}
