package me.taektaek.demoinflearnrestapi.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        //Given
        String name = "Event";
        String description = "Spring";

        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @ParameterizedTest
    @MethodSource("parametersForTestFree")
    @DisplayName("free 초기화하는 update() 테스트")
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    public static Stream<Arguments> parametersForTestFree() {
        return Stream.of(
                Arguments.of(0, 0, true),    //공짜일 때 isFree == true
                Arguments.of(100, 0, false),  //입찰일 때 isFree == false
                Arguments.of(0, 100, false)   //선착순일 때 isFree == false
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForTestOffline")
    @DisplayName("offline 초기화하는 update() 테스트")
    public void testOffline(String location, boolean isOffline) {
        //장소 있으면 offline 필드 값 true
        //Given
        Event event = Event.builder()
                .location(location)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    public static Stream<Arguments> parametersForTestOffline() {
        return Stream.of(
                Arguments.of("강남", true),       //offline 값이 있을 때 isOffline == true
                Arguments.of(null, false),       //offline 값이 null일 때 isOffline == false
                Arguments.of("       ", false)   //offline 값이 공백일 때 isOffline == false
        );
    }

}