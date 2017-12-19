package net.twasi.commands;

import net.twasi.commands.web.CommandHandler;
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

    private List<String> permissionKeys = Arrays.asList("commands.add", "commands.edit", "commands.delete", "commands.list");

    private Permissions defaultPermission = new Permissions(
            Collections.singletonList(
                    new PermissionEntity(
                            PermissionEntityType.GROUP,
                            PermissionGroups.MODERATOR,
                            null
                    )
            ),
            Arrays.asList("commands.add", "commands.edit", "commands.delete", "commands.list"),
            "commands"
    );
    String prefix = "[COMMANDS] ";

    public CommandsPlugin() {
    }

    @Override
    public void onEnable() {
        // Register our own entity
        Database.getMorphia().mapPackageFromClass(CustomCommand.class);

        // Register our own api endpoint
        registerWebHandler("/api/commands", new CommandHandler());
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
                    command.reply(getTranslations().getTranslation(user, "add.usage"));
                    return;
                }

                String[] splitted = command.getMessage().split(" ", 3);

                // Map to our strings
                String name = splitted[1];
                String content = splitted[2];

                // If the command already exists notify the user
                if (CommandStore.createCommand(user, name, content)) {
                    // Reply to the user
                    command.reply(getTranslations().getTranslation(user, "add.successful", name));
                } else {
                    command.reply(getTranslations().getTranslation(user, "add.alreadyExist", name));
                }
            }
        }

        // If the command is edit
        if (command.getCommandName().equalsIgnoreCase("edit")) {
            if (user.hasPermission(command.getSender(), "commands.edit")) {
                // Check length
                if (command.getMessage().split(" ", 3).length != 3) {
                    // Reply with instructions
                    command.reply(getTranslations().getTranslation(user, "edit.usage"));
                    return;
                }

                String[] splitted = command.getMessage().split(" ", 3);

                // Map to our strings
                String name = splitted[1];
                String content = splitted[2];

                // If the command doesnt exist notify the user
                if (CommandStore.editCommand(user, name, content)) {
                    // Reply to the user
                    command.reply(getTranslations().getTranslation(user, "edit.successful", name));
                } else {
                    command.reply(getTranslations().getTranslation(user, "edit.doesntExist", name));
                }
            }
        }

        // If the command is add
        if (command.getCommandName().equalsIgnoreCase("delete")) {
            if (user.hasPermission(command.getSender(), "commands.delete")) {
                // Check length
                if (command.getMessage().split(" ", 2).length != 2) {
                    // Reply with instructions
                    command.reply(getTranslations().getTranslation(user, "delete.usage"));
                    return;
                }

                String[] splitted = command.getMessage().split(" ", 3);

                // Map to our strings
                String name = splitted[1];

                // If the command doesn't exist
                if (CommandStore.deleteCommand(user, name)) {
                    // Reply to the user
                    command.reply(getTranslations().getTranslation(user, "delete.successful", name));
                } else {
                    command.reply(getTranslations().getTranslation(user, "delete.doesntExist", name));
                }
            }
        }

        if (command.getCommandName().equalsIgnoreCase("commands")) {
            if (user.hasPermission(command.getSender(), "commands.list")) {
                List<CustomCommand> commands = CommandStore.getAllCommands(user);
                if (commands == null) {
                    command.reply(getTranslations().getTranslation(user, "commands.noneAvailable"));
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[");

                    String prefix = "";
                    for (CustomCommand cmd : commands) {
                        builder.append(prefix);
                        builder.append(cmd.getName());
                        prefix = ", ";
                    }

                    builder.append("]");
                    command.reply(getTranslations().getTranslation(user, "commands.available", builder.toString()));
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
