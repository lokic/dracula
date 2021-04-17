package com.github.lokic.dracula.entity;


public class Entity<ID> {

    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
