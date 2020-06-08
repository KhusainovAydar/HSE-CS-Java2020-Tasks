package ru.hse.cs.java2020.task03.bot.service.updates.handlers.states;

public enum SearchState implements UserState {
    BEGIN("begin"),
    NEXT("next"),
    PREVIOUS("previous"),
    ;

    private String description;

    SearchState(String description) {
        this.description = description;
    }

    @Override
    public String getStateValue() {
        return description;
    }
}
