package ru.hse.cs.java2020.task03.bot.service.updates.handlers.states;

public enum OAuthState implements UserState {
    GET_TOKEN("get_token"),
    GET_ORG_ID("get_org_id"),
    ERROR("error"),
    ;

    private final String description;

    OAuthState(String description) {
        this.description = description;
    }

    @Override
    public String getStateValue() {
        return description;
    }
}
