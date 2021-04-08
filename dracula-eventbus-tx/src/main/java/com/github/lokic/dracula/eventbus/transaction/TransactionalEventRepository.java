package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionalEventRepository {

    void save(List<TransactionalEvent<? extends Event>> events);

    void updateStatus(TransactionalEvent<? extends Event> event);

    void updateSuccessByEventIds(String editor, LocalDateTime endTime, List<Long> ids);

    List<TransactionalEvent<? extends Event>> queryEvents(LocalDateTime min, LocalDateTime max, int limit);

}
