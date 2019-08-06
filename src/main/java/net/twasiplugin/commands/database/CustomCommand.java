package net.twasiplugin.commands.database;

import net.twasi.core.database.models.BaseEntity;
import net.twasi.core.database.models.User;
import net.twasiplugin.commands.CommandAccessLevel;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity(value = "customCommands", noClassnameStored = true)
public class CustomCommand extends BaseEntity {
    /**
     * Unique ID of the commands
     */
    @Id
    private ObjectId id;

    /**
     * Reference to the user the commands belongs to
     */
    @Reference
    private User user;

    /**
     * The name of the commands
     */
    private String name;

    /**
     * The content of the commands.
     */
    private String content;

    /**
     * Number of uses
     */
    private int uses;

    /**
     * Seconds of cooldown
     */
    private int cooldown;

    /**
     * Access level of the command
     */
    private CommandAccessLevel accessLevel = CommandAccessLevel.VIEWER;

    /**
     * Creates an empty CustomCommand. Used by the database.
     */
    public CustomCommand() {
    }

    /**
     * Creates a new CustomCommand by user, name and content
     *
     * @param user
     * @param name
     * @param content
     * @param accessLevel
     */
    CustomCommand(User user, String name, String content, int cooldown, CommandAccessLevel accessLevel) {
        this.user = user;
        this.name = name;
        this.content = content;
        this.accessLevel = accessLevel;
        this.uses = 0;
        this.cooldown = cooldown;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public User getUser() {
        return user;
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

    public CommandAccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(CommandAccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
