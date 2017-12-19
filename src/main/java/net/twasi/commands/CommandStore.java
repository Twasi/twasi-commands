package net.twasi.commands;

import net.twasi.core.database.Database;
import net.twasi.core.database.models.User;
import net.twasi.core.models.Message.Command;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class CommandStore {

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

    static boolean createCommand(User user, String name, String content) {
        if (getCommandByName(user, name) == null) {
            CustomCommand command = new CustomCommand(user, name.toLowerCase(), content);
            Database.getStore().save(command);
            return true;
        }
        return false;
    }

    static boolean editCommand(User user, String name, String newContent) {
        CustomCommand command = getCommandByName(user, name.toLowerCase());

        if (command == null) {
            return false;
        }

        command.setContent(newContent);
        Database.getStore().save(command);
        return true;
    }

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
