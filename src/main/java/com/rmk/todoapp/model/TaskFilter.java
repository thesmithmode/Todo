package com.rmk.todoapp.model;

public enum TaskFilter {
    ALL("all"),
    ACTIVE("active"),
    COMPLETED("completed");

    private final String value;

    TaskFilter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TaskFilter fromString(String value) {
        if (value == null) {
            return ALL;
        }
        for (TaskFilter filter : values()) {
            if (filter.value.equalsIgnoreCase(value)) {
                return filter;
            }
        }
        return ALL;
    }
}
