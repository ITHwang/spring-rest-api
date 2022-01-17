package me.taektaek.demoinflearnrestapi.events;

import me.taektaek.demoinflearnrestapi.common.ErrorsResource;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        Event event = modelMapper.map(eventDto, Event.class);
        event.update(); //free, offline 필드값 초기화
        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        //refactoring: 링크 추가하는 로직은 resource 객체에 있는 것이 좋음
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create", "profile"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel pagedResources = assembler.toModel(page, new RepresentationModelAssembler<Event, RepresentationModel<?>>() {
            @Override
            public RepresentationModel<?> toModel(Event entity) {
                return new EventResource(entity);
            }
        });

        pagedResources.add(Link.of("/docs/index.html#resources-events-list", "profile"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        //해당 id의 이벤트가 없다면
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) return ResponseEntity.notFound().build();

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get", "profile"));
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors) {

        //수정하려는 이벤트가 없가면 404
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) return ResponseEntity.notFound().build();

        //데이터 바인딩이 이상한 경우 400(비어있는 경우)
        if (errors.hasErrors()) return badRequest(errors);

        //데이터 바인딩이 비즈니스 로직에 맞지 않는 경우
        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        //수정하기
        Event existingEvent = optionalEvent.get();
        this.modelMapper.map(eventDto, existingEvent);
        this.eventRepository.save(existingEvent); //service와 @Transactional이 없으므로 직접 save()로 수정해줌

        EventResource eventResource = new EventResource(existingEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update", "profile"));
        return ResponseEntity.ok(eventResource);
    }

    //Resource wrapping for adding link to index
    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}