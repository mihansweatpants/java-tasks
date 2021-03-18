package org.example.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherCondition {
    private final String name;
    private final String description;
    private final String conditionId;

    public WeatherCondition(@JsonProperty("main") String name,
                            @JsonProperty("description") String description,
                            @JsonProperty("id") String conditionId) {
        this.name = name;
        this.description = description;
        this.conditionId = conditionId;
    }

    public String getConditionId() {
        return conditionId;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        String emoji = "";

        if (conditionId.equals("800")) {
            emoji = "\u2600";
        } else if (conditionId.startsWith("8")) {
            emoji = "\u2601";
        } else if (conditionId.startsWith("3") || conditionId.startsWith("5")) {
            emoji = "\uD83C\uDF27";
        } else if (conditionId.startsWith("2")) {
            emoji = "\u26C8";
        } else if (conditionId.startsWith("6")) {
            emoji = "\uD83C\uDF28";
        } else if (conditionId.startsWith("7")) {
            emoji = "\uD83C\uDF2B";
        }

        return emoji;
    }
}
