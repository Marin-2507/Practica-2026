package com.restaurant.model;

import com.restaurant.interfaces.Printable;

public abstract class BaseEntity implements Printable {
    protected int id;

    public BaseEntity() {}
    public BaseEntity(int id) { this.id = id; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}