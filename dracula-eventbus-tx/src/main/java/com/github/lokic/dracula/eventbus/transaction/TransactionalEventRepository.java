package com.github.lokic.dracula.eventbus.transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionalEventRepository {

    void save(List<TransactionalEvent> events);

    void updateStatus(TransactionalEvent event);

    List<TransactionalEvent> queryEvents(LocalDateTime min, LocalDateTime max, int limit);

}
