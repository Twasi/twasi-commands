package net.twasiplugin.commands.web.model;

import net.twasi.core.database.models.User;
import net.twasiplugin.commands.CustomCommand;

public class CommandDTO {
    private User user;

    private String id;
    private String name;
    private String content;
    private int uses;
    private int cooldown;

    public CommandDTO(User user) {
        this.user = new User();
    }

    public CommandDTO(User user, String id, String name, String content, int uses, int cooldown) {
        this.user = user;
        this.id = id;
        this.name = name;
        this.content = content;
        this.uses = uses;
        this.cooldown = cooldown;
    }

    public CommandDTO(User user, CustomCommand command) {
        this.user = user;
        this.id = command.getId().toString();
        this.name = command.getName();
        this.content = command.getContent();
        this.uses = command.getUses();
        this.cooldown = command.getCooldown();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
