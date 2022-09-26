package com.traffic.eventmanager.controller;

import com.traffic.eventmanager.aop.LogExecutionTime;
import com.traffic.eventmanager.entity.Event;
import com.traffic.eventmanager.entity.request.EventRequestDTO;
import com.traffic.eventmanager.repository.EventRepository;
import com.traffic.eventmanager.service.EventService;
import com.traffic.eventmanager.service.ViolationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@AllArgsConstructor
public class EventController {
    private static final String BASE_URL = "/event";
    private EventRepository eventRepository;
    private ViolationService violationService;
    private EventService eventService;


    /**
     * Post method designed for receiving events from external sources
     * <p>
     * In case of violation, a new entry is added at ViolationRepository @{@link com.traffic.eventmanager.repository.ViolationRepository}
     *
     * @param eventRequestDTO @{@link EventRequestDTO}
     * @return @{@link CompletableFuture<Event>}
     */
    @ApiOperation(value = "Rest method designed for receiving events from external sources")
    @LogExecutionTime
    @PostMapping(path = BASE_URL + "/save")
    public CompletableFuture<Event> addNewEvent(@RequestBody @ApiParam(value = "EventRequestDTO parameteer") EventRequestDTO eventRequestDTO) {

        Event event = eventService.saveEvent(eventRequestDTO);
        violationService.processPossibleViolationFromEvent(event);

        //Mark event as processed
        eventRepository.getEvent(event.getId()).setProcessed(true);

        return CompletableFuture.supplyAsync(() -> event);
    }

}
