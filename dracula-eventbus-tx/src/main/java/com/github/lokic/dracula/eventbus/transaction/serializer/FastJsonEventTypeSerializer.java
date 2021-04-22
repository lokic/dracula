package com.github.lokic.dracula.eventbus.transaction.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.EventTypeSerializer;

public class FastJsonEventTypeSerializer implements EventTypeSerializer {

    @Override
    public <E extends Event> E deserialize(String s, Class<E> eventClazz) {
        return JSON.parseObject(s, eventClazz);
    }

    @Override
    public String serialize(Event event) {
        return JSON.toJSONString(event, SerializerFeature.WriteClassName);
    }
}
