package net.twasi.commands;

import net.twasi.core.database.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity(value = "customCommands", noClassnameStored = true)
public class CustomCommand {
    @Id
    private ObjectId id;

    @Reference
    private User user;

    private String name;

    private String content;

    public CustomCommand() {}

    public CustomCommand(User user, String name, String content) {
        this.user = user;
        this.name = name;
        this.content = content;
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
}
