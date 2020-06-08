package ru.hse.cs.java2020.task03.bot.service.updates.handlers.states;

public enum WatchState implements UserState {
    BEGIN("begin"),
    GET_KEY("get_key"),
    WAIT_BACK("wait_back"),
    SHOW_FROM_CB("show_from_cb"),
    SHOW_COMMENTS("show_comments"),
    INTRO_COMMENT("intro_comment"),
    GET_COMMENT("get_comment"),
    ;

    private final String description;

    WatchState(String description) {
        this.description = description;
    }

    @Override
    public String getStateValue() {
        return description;
    }
}
