package com.traffic.eventmanager.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.traffic.eventmanager.entity.Event;
import com.traffic.eventmanager.entity.enums.EventType;
import com.traffic.eventmanager.entity.request.EventRequestDTO;
import com.traffic.eventmanager.repository.EventRepository;
import com.traffic.eventmanager.repository.ViolationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class EventIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ViolationRepository violationRepository;

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void performSaveEvent_WithTurnViolation() throws Exception {
        eventRepository.deleteAllEvents();
        violationRepository.deleteAllViolations();

        EventRequestDTO eventRequestDTO = EventRequestDTO.builder()
                .eventType("turn")
                .eventDate(LocalDateTime.now())
                .licensePlate("AB123")
                .limit(20d)
                .speed(20d).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/event/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(eventRequestDTO))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Event event = (Event) mvcResult.getAsyncResult();

        assertNull(event.getUnit());
        assertEquals(EventType.TURN, event.getEventType());
        assertTrue(event.getProcessed());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals(1, violationRepository.findAll().size());
    }

    @Test
    void performSaveEvent_WithNoViolation() throws Exception {
        eventRepository.deleteAllEvents();
        violationRepository.deleteAllViolations();

        EventRequestDTO eventRequestDTO = EventRequestDTO.builder()
                .eventType("speed")
                .eventDate(LocalDateTime.now())
                .licensePlate("AB123")
                .limit(20d)
                .speed(20d).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/event/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(eventRequestDTO))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Event event = (Event) mvcResult.getAsyncResult();

        assertNull(event.getUnit());
        assertEquals(EventType.SPEED, event.getEventType());
        assertTrue(event.getProcessed());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals(0, violationRepository.findAll().size());
    }

    @Test
    void performSaveEvent_WithNoKnownType() throws Exception {
        eventRepository.deleteAllEvents();
        violationRepository.deleteAllViolations();

        EventRequestDTO eventRequestDTO = EventRequestDTO.builder()
                .eventType("typenotknown")
                .eventDate(LocalDateTime.now())
                .licensePlate("AB123")
                .limit(20d)
                .speed(20d).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/event/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(eventRequestDTO))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Event event = (Event) mvcResult.getAsyncResult();

        assertNull(event.getUnit());
        assertEquals(EventType.OTHER, event.getEventType());
        assertTrue(event.getProcessed());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals(0, violationRepository.findAll().size());
    }

}