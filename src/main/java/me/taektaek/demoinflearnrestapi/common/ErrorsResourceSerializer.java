package me.taektaek.demoinflearnrestapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonComponent
public class ErrorsResourceSerializer extends JsonSerializer<ErrorsResource> {

    @Override
    public void serialize(ErrorsResource errorsResource, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {

        gen.writeStartObject();

        try {
            gen.writeObjectField("content", errorsResource.getContent());

            //href가 들어있는 객체를 리스트에서 빼냄
            List<Link> list = new ArrayList<>();
            Iterator<Link> iterator = errorsResource.getLinks().stream().iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }

            gen.writeFieldName("_links");
            gen.writeStartObject();
            gen.writeObjectField("index", list.get(0));
            gen.writeEndObject();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        gen.writeEndObject();
    }
}
