package com.medialab.models;

import java.util.Objects;

public class Category {
    private String title;

    public Category(String title) { this.title = title; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return Objects.equals(title, category.title);
    }

    @Override
    public int hashCode() { return Objects.hash(title); }

    @Override
    public String toString() { return title; }
}