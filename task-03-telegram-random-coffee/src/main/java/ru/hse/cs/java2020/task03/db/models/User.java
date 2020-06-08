package ru.hse.cs.java2020.task03.db.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQuery;

@NamedQuery(name = "User_findByChatId", query = "from User where chat_id = :chatId")

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "org_id")
    private String orgId;

    private String uid;

    @Column(name = "state_class")
    private String stateClass;

    @Column(name = "state_value")
    private String stateValue;

    private String meta;

    public User() {
    }

    public User(String token,
                String chatId,
                String orgId,
                String uid,
                String stateClass,
                String stateValue,
                String meta) {
        this.token = token;
        this.chatId = chatId;
        this.orgId = orgId;
        this.uid = uid;
        this.stateClass = stateClass;
        this.stateValue = stateValue;
        setMeta(meta);
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta == null ? "{}" : meta;
    }

    public String getStateClass() {
        return stateClass;
    }

    public void setStateClass(String stateClass) {
        this.stateClass = stateClass;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "models.User{"
                + "id=" + id
                + ", token='" + token + '\''
                + ", chat_id='" + chatId + '\''
                + ", org_id='" + orgId + '\''
                + ", uid='" + uid + '\''
                + ", state_class='" + stateClass + '\''
                + ", state_value='" + stateValue + '\''
                + "}";
    }
}
