package com.traffic.eventmanager.service;

import com.traffic.eventmanager.entity.Event;
import com.traffic.eventmanager.entity.enums.EventType;
import com.traffic.eventmanager.entity.request.EventRequestDTO;
import com.traffic.eventmanager.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    private EventService underTest;
    @Mock
    private EventRepository eventRepository;

    private String eventId;

    @BeforeEach
    void setUp() {
        underTest = new EventService(eventRepository);
        eventId = UUID.randomUUID().toString();
    }

    @Test
    void processEvent_EventTypeNotExistent() {
        EventRequestDTO eventRequestDTO = EventRequestDTO.builder()
                .eventType("asd")
                .eventDate(LocalDateTime.now())
                .licensePlate("AB123")
                .limit(20d)
                .speed(20d).build();

        Event event = underTest.saveEvent(eventRequestDTO);
        assertEquals(EventType.OTHER, event.getEventType());
    }

    @Test
    void processEvent_EventTypeNull() {
        EventRequestDTO eventRequestDTO = EventRequestDTO.builder()
                .eventType(null)
                .eventDate(LocalDateTime.now())
                .licensePlate("AB123")
                .limit(20d)
                .speed(20d).build();

        Event event = underTest.saveEvent(eventRequestDTO);
        assertEquals(EventType.NONE, event.getEventType());
    }

    @Test
    void processEvent_EventTypeTurn() {
        EventRequestDTO eventRequestDTO = EventRequestDTO.builder()
                .eventType("turn")
                .eventDate(LocalDateTime.now())
                .licensePlate("AB123")
                .limit(20d)
                .speed(20d).build();

        Event event = underTest.saveEvent(eventRequestDTO);
        assertEquals(EventType.TURN, event.getEventType());
        assertEquals(event.getLimit(), eventRequestDTO.getLimit());
        assertEquals(false, event.getProcessed());
        assertEquals(event.getSpeed(), eventRequestDTO.getSpeed());
        assertEquals(event.getLicensePlate(), eventRequestDTO.getLicensePlate());
        assertEquals(event.getUnit(), eventRequestDTO.getUnit());
    }

}
