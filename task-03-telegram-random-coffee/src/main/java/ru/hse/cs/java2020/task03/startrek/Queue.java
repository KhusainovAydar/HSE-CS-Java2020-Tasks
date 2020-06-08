package ru.hse.cs.java2020.task03.startrek;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
        "assignAuto",
        "denyVoting",
        "denyConductorAutolink",
        "denyTrackerAutolink",
        "defaultPriority",
        "defaultType",
        "description",
        "issueTypes",
        "issueTypesConfig",
        "lead",
        "name",
        "teamUsers",
        "version",
        "versions",
        "workflows",
        "updatedAt",
        "useComponentPermissionsIntersection",
        "useLastSignature",
})
public class Queue {
    private String self;
    private String id;
    private String key;
    private String display;

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

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
