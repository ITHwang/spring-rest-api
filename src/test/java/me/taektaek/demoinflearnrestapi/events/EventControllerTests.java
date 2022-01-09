package me.taektaek.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 1, 7, 19, 36, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 1, 8, 19, 36, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 9, 20, 11, 23))
                .endEventDateTime(LocalDateTime.of(2022, 11, 23, 12, 32, 43))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남의 D2 스타스업 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect((header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("free").value(false))
        ;
    }

    @Test
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100) //bad
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEventDateTime(LocalDateTime.of(2022, 1, 7, 19, 36, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 1, 8, 19, 36, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 9, 20, 11, 23))
                .endEventDateTime(LocalDateTime.of(2022, 11, 23, 12, 32, 43))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남의 D2 스타스업 팩토리")
                .free(true) //bad
                .offline(false) //bad
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
}
