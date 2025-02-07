package com.medialab.models;

import java.util.Objects;

public class PriorityLevel {
    private String name;

    public PriorityLevel(String name) { this.name = name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PriorityLevel priority = (PriorityLevel) obj;
        return Objects.equals(name, priority.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() { return name; }
}