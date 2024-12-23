package ru.practicum.mapper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import ru.practicum.constants.Constants;

import java.io.IOException;
import java.time.LocalDateTime;

@JsonComponent
public class DateDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser,
                                     DeserializationContext context) throws IOException, JacksonException {
        return LocalDateTime.parse(jsonParser.getValueAsString(), Constants.DATE_FORMATTER);
    }
}
