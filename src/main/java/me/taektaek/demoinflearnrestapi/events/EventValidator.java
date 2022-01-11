package me.taektaek.demoinflearnrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

//javax.validation.constraint만으로는 유효성을 검사하기 어려우므로 커스터마이징한다.
@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) { //price validate
            errors.reject("wrongPrices", "Values of prices are wrong.");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime(); //endEventDateTime validate
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong.");
        }

        // TODO: beginEventDateTime
        // TODO: CloseEnrollmentDateTime

    }
}
