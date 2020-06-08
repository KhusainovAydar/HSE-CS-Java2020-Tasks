package ru.hse.cs.java2020.task03.bot.service.updates.handlers.states;

public enum CreateState implements UserState {
    BEGIN("begin"),
    INTRO_SUMMARY("intro_summary"),
    GET_SUMMARY("get_summary"),
    INTRO_DESCRIPTION("intro_description"),
    GET_DESCRIPTION("get_description"),
    ME_ASSIGNER("me_assigner"),
    NO_ASSIGNER("no_assigner"),
    UPDATE_CHANGES("update_changes"),
    INTRO_QUEUE("intro_queue"),
    GET_QUEUE("get_queue"),
    ;

    private String description;

    CreateState(String description) {
        this.description = description;
    }

    @Override
    public String getStateValue() {
        return description;
    }
}
