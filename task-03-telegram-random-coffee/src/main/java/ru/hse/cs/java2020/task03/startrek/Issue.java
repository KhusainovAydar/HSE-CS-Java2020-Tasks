package ru.hse.cs.java2020.task03.startrek;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
        "commentWithExternalMessageCount",
        "commentWithoutExternalMessageCount",
        "createdAt",
        "deadline",
        "priority",
        "parent",
        "storyPoints",
        "status",
        "statusStartTime",
        "tags",
        "type",
        "version",
        "votes",
        "updatedAt",
})
public class Issue {
    private String self;
    private String id;
    private String key;
    private String summary;
    private String description;

    private List<Person> followers;
    private Person updatedBy;
    private Person createdBy;
    private Person assignee;
    private Boolean favorite;
    private Queue queue;

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public String getQueueId() {
        return queue.getId();
    }

    public String getQueueKey() {
        return queue.getKey();
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUpdatedBy(Person updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public void setFollowers(List<Person> followers) {
        this.followers = followers;
    }

    public String getSelf() {
        return self;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setAssignee(Person assignee) {
        this.assignee = assignee;
    }

    public String getAuthor() {
        return createdBy.getDisplay();
    }

    public String getAssigner() {
        return assignee == null ? "Не назначен" : assignee.getDisplay();
    }

    public List<String> getFollowers() {
        if (followers == null) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (Person follower : followers) {
            result.add(follower.getDisplay());
        }
        return result;
    }
}
