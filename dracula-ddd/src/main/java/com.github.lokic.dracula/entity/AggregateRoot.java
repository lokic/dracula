package com.github.lokic.dracula.entity;


import com.github.lokic.dracula.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public class AggregateRoot<ID> extends Entity<ID> {

    public List<DomainEvent> events;

    public void addDomainEvent(DomainEvent event) {
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
    }

    public void removeDomainEvent(DomainEvent event) {
        if (events != null) {
            events.remove(event);
        }
    }

    public List<DomainEvent> getDomainEvents() {
        return events;
    }

    public void clearDomainEvents() {
        if (events != null) {
            events.clear();
        }
    }


}
