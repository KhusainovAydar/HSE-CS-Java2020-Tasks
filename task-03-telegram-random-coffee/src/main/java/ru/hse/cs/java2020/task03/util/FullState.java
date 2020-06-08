package ru.hse.cs.java2020.task03.util;

import ru.hse.cs.java2020.task03.db.models.User;

public class FullState {
    private String className;
    private String stateValue;

    public FullState(String className, String stateValue) {
        this.className = className;
        this.stateValue = stateValue;
    }

    public static void fillUserState(User user, FullState someState) {
        user.setStateClass(someState.className);
        user.setStateValue(someState.stateValue);
    }

    public String getClassName() {
        return className;
    }

    public String getStateValue() {
        return stateValue;
    }
}
