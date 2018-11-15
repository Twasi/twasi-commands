package net.twasiplugin.commands;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class CommandRepository extends Repository<CustomCommand> {

    /**
     * Gets a command by a user and command name
     * @param user the user to search the command for (twitch channel)
     * @param name the name of the command
     * @return the command if found, null otherwise
     */
    public CustomCommand getCommandByName(User user, String name) {
        Query<CustomCommand> q = store.createQuery(CustomCommand.class);
        List<CustomCommand> command = q
                .field("name").equal(name.toLowerCase())
                .field("user").equal(user)
                .asList();
        if (command.size() == 0) {
            return null;
        }
        return command.get(0);
    }

    public CustomCommand getCommandById(User user, String id) {
        Query<CustomCommand> q = store.createQuery(CustomCommand.class);
        List<CustomCommand> command = q
                .field("_id").equal(new ObjectId(id))
                .field("user").equal(user)
                .asList();
        if (command.size() == 0) {
            return null;
        }
        return command.get(0);
    }

    /**
     * Creates a command for a user
     * @param user the user to create the command for (twitch channel)
     * @param name the name of the command to create
     * @param content the content of the new command
     * @return the id of the created command on success, null otherwise
     */
    public String createCommand(User user, String name, String content) {
        if (getCommandByName(user, name) == null) {
            CustomCommand command = new CustomCommand(user, name.toLowerCase(), content);
            store.save(command);
            return getCommandByName(user, name).getId().toString();
        }
        return null;
    }

    /**
     * Changes a command for a user
     * @param user the user to change the command for (twitch channel)
     * @param id the id of the command
     * @param name the (new) name of the command
     * @param content the (new) content of the command
     * @return true if the command was updated, false otherwise
     */
    public boolean editCommand(User user, String id, String name, String content) {
        CustomCommand command = getCommandById(user, id);

        if (command == null) {
            return false;
        }

        if (name == null || name.length() == 0) {
            return false;
        }

        if (content == null || content.length() == 0) {
            return false;
        }

        command.setContent(content);
        command.setName(name);

        store.save(command);
        return true;
    }
    public boolean editCommandByName(User user, String name, String content) {
        CustomCommand cmd = getCommandByName(user, name);
        return editCommand(user, cmd.getId().toString(), name, content);
    }

    /**
     * Delets a command by a user and id
     * @param user the user to delete the command for (twitch channel)
     * @param id the id of the command
     * @return true if the command was deleted, false otherwise
     */
    public boolean deleteCommand(User user, String id) {
        CustomCommand command = getCommandById(user, id);

        if (command == null) {
            return false;
        }

        store.delete(command);
        return true;
    }
    public boolean deleteCommandByName(User user, String name) {
        CustomCommand cmd = getCommandByName(user, name);
        return deleteCommand(user, cmd.getId().toString());
    }

    /**
     * Return all commands by a useer
     * @param user the user to look up
     * @return all commands by the user
     */
    public List<CustomCommand> getAllCommands(User user) {
        List<CustomCommand> commands = store.createQuery(CustomCommand.class)
                .field("user").equal(user).asList();
        if (commands.size() == 0) {
            return new ArrayList<>();
        }
        return commands;
    }
}
