package net.twasiplugin.commands.web.model;

import net.twasiplugin.commands.CommandAccessLevel;

public class TwasiCommandAccessLevelDTO {

    private int value;
    private String name;

    public TwasiCommandAccessLevelDTO(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public TwasiCommandAccessLevelDTO(CommandAccessLevel accessLevel) {
        this(accessLevel.getLevel(), accessLevel.name());
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
