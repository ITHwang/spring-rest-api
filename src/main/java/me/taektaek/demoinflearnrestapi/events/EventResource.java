package me.taektaek.demoinflearnrestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel {

    public EventResource(Event event, Link... links) {
        super(event, (Iterable) Arrays.asList(links));
        //self 링크를 여기서 넣어준다.
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
