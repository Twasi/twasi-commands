package net.twasiplugin.commands;

import net.twasi.core.database.Database;
import net.twasi.core.database.models.User;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class CommandStore {

    /**
     * Gets a command by a user and command name
     * @param user the user to search the command for (twitch channel)
     * @param name the name of the command
     * @return the command if found, null otherwise
     */
    static CustomCommand getCommandByName(User user, String name) {
        Query<CustomCommand> q = Database.getStore().createQuery(CustomCommand.class);
        List<CustomCommand> command = q
                .field("name").equal(name.toLowerCase())
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
     * @return true if the command was created, false otherwise
     */
    static boolean createCommand(User user, String name, String content) {
        if (getCommandByName(user, name) == null) {
            CustomCommand command = new CustomCommand(user, name.toLowerCase(), content);
            Database.getStore().save(command);
            return true;
        }
        return false;
    }

    /**
     * Changes a command for a user
     * @param user the user to change the command for (twitch channel)
     * @param name the name of the command
     * @param newContent the new content of the command
     * @return true if the command was updated, false otherwise
     */
    static boolean editCommand(User user, String name, String newContent) {
        CustomCommand command = getCommandByName(user, name.toLowerCase());

        if (command == null) {
            return false;
        }

        command.setContent(newContent);
        Database.getStore().save(command);
        return true;
    }

    /**
     * Delets a command by a user and name
     * @param user the user to delete the command for (twitch channel)
     * @param name the name of the command
     * @return true if the command was deleted, false otherwise
     */
    static boolean deleteCommand(User user, String name) {
        CustomCommand command = getCommandByName(user, name.toLowerCase());

        if (command == null) {
            return false;
        }

        Database.getStore().delete(command);
        return true;
    }

    public static List<CustomCommand> getAllCommands(User user) {
        List<CustomCommand> commands = Database.getStore().createQuery(CustomCommand.class)
                .field("user").equal(user).asList();
        if (commands.size() == 0) {
            return new ArrayList<>();
        }
        return commands;
    }

}
