package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionEventRepository {

    void save(List<TransactionEvent<? extends Event>> events);

    void updateStatus(TransactionEvent<? extends Event> event);

    void updateSuccessByEventIds(String editor, LocalDateTime endTime, List<Long> ids);

    List<TransactionEvent<? extends Event>> queryEvents(LocalDateTime min, LocalDateTime max, int limit);

}
