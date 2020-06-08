package ru.hse.cs.java2020.task03.bot.service.updates.handlers.states;

public enum ChooseState implements UserState {
    BEGIN("begin"),
    ;

    private final String description;

    ChooseState(String description) {
        this.description = description;
    }

    @Override
    public String getStateValue() {
        return description;
    }
}
