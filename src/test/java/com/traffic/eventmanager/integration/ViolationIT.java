package com.traffic.eventmanager.integration;

import com.traffic.eventmanager.entity.RedLightViolation;
import com.traffic.eventmanager.entity.SpeedViolation;
import com.traffic.eventmanager.entity.Violation;
import com.traffic.eventmanager.entity.response.ViolationResponseDTO;
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

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class ViolationIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ViolationRepository violationRepository;


    @Test
    void getViolationList() throws Exception {
        violationRepository.deleteAllViolations();
        Violation violation = new RedLightViolation(UUID.randomUUID());
        violation.setEventId(UUID.randomUUID());
        violationRepository.save(violation);
        violation = new SpeedViolation(UUID.randomUUID());
        violation.setEventId(UUID.randomUUID());
        violationRepository.save(violation);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/violation")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Collection<Violation> violations = (Collection<Violation>) mvcResult.getAsyncResult();

        assertEquals(2, violations.size());
        assertEquals(2, violationRepository.findAll().size());
    }


    @Test
    void payViolation_WithSmallerAmount() throws Exception {

        Violation violation = new RedLightViolation(UUID.randomUUID());
        violation.setEventId(UUID.randomUUID());
        violationRepository.save(violation);


        mockMvc.perform(MockMvcRequestBuilders.put("/violation")
                        .queryParam("violationid", violation.getId().toString())
                        .queryParam("sum", violation.getFine() - 1 + "")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn();

//        Collection<Violation> violations = (Collection<Violation>) mvcResult.getAsyncResult();
//
//        assertEquals(2, violations.size());
//        assertTrue(violations.stream().findFirst().get() instanceof RedLightViolation);
//        assertTrue(violations.stream().toList().get(1) instanceof SpeedViolation);
//        assertEquals(2, violationRepository.findAll().size());
    }

    @Test
    void payViolation_WithNonExistingId() throws Exception {
        violationRepository.deleteAllViolations();


        mockMvc.perform(MockMvcRequestBuilders.put("/violation")
                        .queryParam("violationid", UUID.randomUUID().toString())
                        .queryParam("sum", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn();

//        Collection<Violation> violations = (Collection<Violation>) mvcResult.getAsyncResult();
//
//        assertEquals(2, violations.size());
//        assertTrue(violations.stream().findFirst().get() instanceof RedLightViolation);
//        assertTrue(violations.stream().toList().get(1) instanceof SpeedViolation);
//        assertEquals(2, violationRepository.findAll().size());
    }

    @Test
    void payViolation_WithOkValues() throws Exception {
        violationRepository.deleteAllViolations();
        Violation violation = new RedLightViolation(UUID.randomUUID());
        violation.setEventId(UUID.randomUUID());
        violationRepository.save(violation);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/violation")
                        .queryParam("violationid", violation.getId().toString())
                        .queryParam("sum", violation.getFine() * 2 + "")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Violation responseViolation = (Violation) mvcResult.getAsyncResult();
        assertTrue(responseViolation.getPaid());
        assertEquals(responseViolation.getId(), violation.getId());
    }


    @Test
    void getSummary() throws Exception {
        Double paid = 0d;
        Double unpaid = 0d;
        violationRepository.deleteAllViolations();
        Violation violation = new RedLightViolation(UUID.randomUUID());
        violation.setEventId(UUID.randomUUID());
        violation.setPaid(true);
        paid += violation.getFine();
        violationRepository.save(violation);
        violation = new SpeedViolation(UUID.randomUUID());
        violation.setEventId(UUID.randomUUID());
        violation.setPaid(true);
        paid += violation.getFine();
        violationRepository.save(violation);
        violation = new SpeedViolation(UUID.randomUUID());
        violation.setEventId(UUID.randomUUID());
        violation.setPaid(false);
        unpaid += violation.getFine();
        violationRepository.save(violation);


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/violation/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ViolationResponseDTO violationResponseDTO = (ViolationResponseDTO) mvcResult.getAsyncResult();
        assertEquals(violationResponseDTO.getPaidViolations(), paid);
        assertEquals(violationResponseDTO.getUnpaidViolations(), unpaid);
    }

}
