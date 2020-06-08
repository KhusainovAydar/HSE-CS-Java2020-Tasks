package ru.hse.cs.java2020.task03.startrek;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
        "disableNotifications",
        "dismissed",
        "email",
        "external",
        "firstName",
        "login",
        "lastName",
        "hasLicense",
        "useNewFilters",
})
public class Person {
    private String self;
    private String id;
    private String display;
    private String uid;

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display == null ? "Не назначен" : display;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getSelf() {
        return self;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
