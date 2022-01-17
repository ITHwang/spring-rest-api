package me.taektaek.demoinflearnrestapi.events;

import me.taektaek.demoinflearnrestapi.common.BaseControllerTest;
import me.taektaek.demoinflearnrestapi.common.RestDocsConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
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
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(linkWithRel("self").description("link to self"),
                              linkWithRel("query-events").description("link to query events"),
                              linkWithRel("update-event").description("link to update an existing event"),
                              linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment ")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment "),
                                fieldWithPath("offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
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
    @DisplayName("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 1, 7, 19, 36, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 1, 5, 19, 36, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 9, 20, 11, 23))
                .endEventDateTime(LocalDateTime.of(2022, 11, 8, 12, 32, 43))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남의 D2 스타스업 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트로 10개씩 페이지 만들고, 두 번째 페이지 조회")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        //When & Then
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[].id").description("Identifier of event"),
                                fieldWithPath("_embedded.eventList[].name").description("Name of event"),
                                fieldWithPath("_embedded.eventList[].description").description("description of event"),
                                fieldWithPath("_embedded.eventList[].beginEnrollmentDateTime").description("date time of begin of event"),
                                fieldWithPath("_embedded.eventList[].closeEnrollmentDateTime").description("date time of close of event"),
                                fieldWithPath("_embedded.eventList[].beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("_embedded.eventList[].endEventDateTime").description("date time of end of event"),
                                fieldWithPath("_embedded.eventList[].location").description("location of event"),
                                fieldWithPath("_embedded.eventList[].basePrice").description("base price of event"),
                                fieldWithPath("_embedded.eventList[].maxPrice").description("max price of event"),
                                fieldWithPath("_embedded.eventList[].limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("_embedded.eventList[].offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("_embedded.eventList[].free").description("it tells is this event is free or not"),
                                fieldWithPath("_embedded.eventList[].eventStatus").description("event status"),
                                fieldWithPath("_embedded.eventList[]_links.self.href").description("link to self"),
                                fieldWithPath("page.size").description("elements per page"),
                                fieldWithPath("page.totalElements").description("total elements"),
                                fieldWithPath("page.totalPages").description("total pages"),
                                fieldWithPath("page.number").description("page number that starts from zero"),
                                fieldWithPath("_links.first.href").description("link to first page"),
                                fieldWithPath("_links.prev.href").description("link to previous page"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.next.href").description("link to next page"),
                                fieldWithPath("_links.last.href").description("link to last page"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
//                        links(
//                                halLinks(),
//                                linkWithRel("first").description("link to first page"),
//                                linkWithRel("prev").description("link to previous page"),
//                                linkWithRel("self").description("link to self"),
//                                linkWithRel("next").description("link to next page"),
//                                linkWithRel("last").description("link to last page"),
//                                linkWithRel("profile").description("link to profile")
//                        )
                ))
        ;
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-event",
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Identifier of event"),
                                fieldWithPath("name").description("Name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("endEventDateTime").description("date time of end of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답 받기")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/999999999"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("기존의 이벤트를 수정하기")
    public void updateEvent() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        String eventName = "Updated Event";
        eventDto.setName(eventName);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of updated event"),
                                fieldWithPath("description").description("description of updated event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of updated event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of updated event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of updated event"),
                                fieldWithPath("endEventDateTime").description("date time of end of updated event"),
                                fieldWithPath("location").description("location of updated event"),
                                fieldWithPath("basePrice").description("base price of updated event"),
                                fieldWithPath("maxPrice").description("max price of updated event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment ")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Identifier of event"),
                                fieldWithPath("name").description("Name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("endEventDateTime").description("date time of end of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        //When & Then
        this.mockMvc.perform(put("/api/events/123123")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }


    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 1, 7, 19, 36, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 1, 8, 19, 36, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 9, 20, 11, 23))
                .endEventDateTime(LocalDateTime.of(2022, 11, 23, 12, 32, 43))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남의 D2 스타스업 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }

}
