package ru.hse.cs.java2020.task03.startrek;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
        "updatedBy",
        "createdAt",
        "updatedAt",
        "transport",
        "type",
        "longId",
        "version",
})
public class Comment {
    public String self;
    public String id;
    public String text;

    private Person createdBy;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getSelf() {
        return self;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }

    public String getAuthor() {
        return createdBy.getDisplay();
    }

}
