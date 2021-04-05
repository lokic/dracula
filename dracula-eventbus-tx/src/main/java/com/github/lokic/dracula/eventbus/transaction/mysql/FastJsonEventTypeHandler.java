package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.EventTypeHandler;

public class FastJsonEventTypeHandler implements EventTypeHandler {

    @Override
    public Event deserialize(String s) {
        return JSON.parseObject(s, Event.class);
    }

    @Override
    public String serialize(Event event) {
        return JSON.toJSONString(event, SerializerFeature.WriteClassName);
    }
}
