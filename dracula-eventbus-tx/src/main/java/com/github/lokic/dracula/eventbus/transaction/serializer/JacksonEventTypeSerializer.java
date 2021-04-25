package com.github.lokic.dracula.eventbus.transaction.serializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.EventTypeSerializer;
import lombok.SneakyThrows;

public class JacksonEventTypeSerializer implements EventTypeSerializer {

    private ObjectMapper objectMapper;

    public JacksonEventTypeSerializer() {
        objectMapper = defaultObjectMapper();
    }

    private ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return om;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public <E extends Event> E deserialize(String s, Class<E> eventClazz) {
        return objectMapper.readValue(s, eventClazz);
    }

    @SneakyThrows
    @Override
    public String serialize(Event event) {
        return objectMapper.writeValueAsString(event);
    }
}
