package com.medialab.models;

import java.util.Objects;

public class PriorityLevel {
    private String title;

    public PriorityLevel(String title) { this.title = title; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PriorityLevel priority = (PriorityLevel) obj;
        return Objects.equals(title, priority.title);
    }

    @Override
    public int hashCode() { return Objects.hash(title); }

    @Override
    public String toString() { return title; }
}