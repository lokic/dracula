package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.EventTypeSerializer;
import lombok.SneakyThrows;

public class JacksonEvenTypeSerializer implements EventTypeSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }


    @SneakyThrows
    @Override
    public <E extends Event> E deserialize(String s, Class<E> eventClazz) {
        return OBJECT_MAPPER.readValue(s, eventClazz);
    }

    @SneakyThrows
    @Override
    public String serialize(Event event) {
        return OBJECT_MAPPER.writeValueAsString(event);
    }
}
