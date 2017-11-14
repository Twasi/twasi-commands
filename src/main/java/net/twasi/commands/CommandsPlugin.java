package net.twasi.commands;

import net.twasi.core.database.Database;
import net.twasi.core.database.models.User;
import net.twasi.core.database.models.permissions.PermissionEntity;
import net.twasi.core.database.models.permissions.PermissionEntityType;
import net.twasi.core.database.models.permissions.PermissionGroups;
import net.twasi.core.database.models.permissions.Permissions;
import net.twasi.core.interfaces.api.TwasiInterface;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.models.Message.Command;
import net.twasi.core.models.Message.Message;
import net.twasi.core.plugin.api.TwasiPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandsPlugin extends TwasiPlugin {

    private List<String> permissionKeys = Arrays.asList("commands.add", "commands.edit", "commands.delete");

    private Permissions defaultPermission = new Permissions(
            Collections.singletonList(
                    new PermissionEntity(
                            PermissionEntityType.GROUP,
                            PermissionGroups.MODERATOR,
                            null
                    )
            ),
            Arrays.asList("commands.add", "commands.edit", "commands.delete"),
            "commands"
    );
    public String prefix = "[COMMANDS] ";

    public CommandsPlugin() {
    }

    @Override
    public void onEnable() {
        // Register our own entity
        Database.getMorphia().mapPackageFromClass(CustomCommand.class);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onInstall(TwasiInterface inf) {
        // Get the user we are installing for
        User user = inf.getStreamer().getUser();

        // Copy the list of permissions to not generate a ConcurrentListModificationException
        List<Permissions> newPermissions = new ArrayList<>(user.getPermissions());

        // If not all keys exist anywhere in the database
        if (!user.doAllPermissionKeysExist(permissionKeys)) {
            TwasiLogger.log.info(prefix + "Not all permissions found. Created default ones.");

            // Go trough every permission object
            for (Permissions perm : user.getPermissions()) {
                // If he name is commands, delete it
                if (perm.getName() != null && perm.getName().equalsIgnoreCase("commands")) {
                    TwasiLogger.log.info(prefix + "Overwritten permission with name commands");
                    newPermissions.remove(perm);
                }
            }

            // Set our (probably) removed permissions back to the user
            user.setPermissions(newPermissions);

            // Add the default permissions
            user.getPermissions().add(defaultPermission);

            // Save everything
            Database.getStore().save(user);
        } else {
            TwasiLogger.log.debug(prefix + " All permissions keys exist. We are ready to go!");
        }
    }

    @Override
    public void onCommand(Command command) {
        User user = command.getTwasiInterface().getStreamer().getUser();

        // If the command is add
        if (command.getCommandName().equalsIgnoreCase("add")) {
            if (user.hasPermission(command.getSender(), "commands.add")) {
                // Check length
                if (command.getMessage().split(" ", 3).length != 3) {
                    // Reply with instructions
                    command.reply("Usage: !add [command] [content] Example for !bot: !add !bot Twasi is cool!");
                    return;
                }

                String[] splitted = command.getMessage().split(" ", 3);

                // Map to our strings
                String name = splitted[1];
                String content = splitted[2];

                // If the command already exists notify the user
                if (CommandStore.createCommand(user, name, content)) {
                    // Reply to the user
                    command.reply("The command " + name + " was added successfully.");
                } else {
                    command.reply("The command " + name + " does already exist.");
                }
            }
        }

        // If the command is edit
        if (command.getCommandName().equalsIgnoreCase("edit")) {
            if (user.hasPermission(command.getSender(), "commands.edit")) {
                // Check length
                if (command.getMessage().split(" ", 3).length != 3) {
                    // Reply with instructions
                    command.reply("Usage: !edit [command] [new content] Example for !bot: !edit !bot Twasi is the most expandable bot!");
                    return;
                }

                String[] splitted = command.getMessage().split(" ", 3);

                // Map to our strings
                String name = splitted[1];
                String content = splitted[2];

                // If the command doesnt exist notify the user
                if (CommandStore.editCommand(user, name, content)) {
                    // Reply to the user
                    command.reply("The command " + name + " was edited successfully.");
                } else {
                    command.reply("The command " + name + " doesn't exist.");
                }
            }
        }

        // If the command is add
        if (command.getCommandName().equalsIgnoreCase("delete")) {
            if (user.hasPermission(command.getSender(), "commands.delete")) {
                // Check length
                if (command.getMessage().split(" ", 2).length != 2) {
                    // Reply with instructions
                    command.reply("Usage: !delete [command] Example for !bot: !delete !bot");
                    return;
                }

                String[] splitted = command.getMessage().split(" ", 3);

                // Map to our strings
                String name = splitted[1];

                // If the command doesn't
                if (CommandStore.deleteCommand(user, name)) {
                    // Reply to the user
                    command.reply("The command " + name + " was deleted successfully.");
                } else {
                    command.reply("The command " + name + " doesn't exist.");
                }
            }
        }
    }

    @Override
    public void onMessage(Message msg) {
        User user = msg.getTwasiInterface().getStreamer().getUser();

        String[] splitted = msg.getMessage().split(" ");
        String name = splitted[0];

        CustomCommand command = CommandStore.getCommandByName(user, name);

        if (command != null) {
            msg.reply(command.getContent());
        }
    }
}
