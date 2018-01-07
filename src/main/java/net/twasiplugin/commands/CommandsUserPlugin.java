package net.twasiplugin.commands;

import net.twasi.core.database.Database;
import net.twasi.core.database.models.User;
import net.twasi.core.database.models.permissions.Permissions;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.models.Message.TwasiCommand;
import net.twasi.core.models.Message.TwasiMessage;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.events.TwasiCommandEvent;
import net.twasi.core.plugin.api.events.TwasiInstallEvent;
import net.twasi.core.plugin.api.events.TwasiMessageEvent;
import net.twasi.core.plugin.api.events.TwasiUninstallEvent;

import java.util.List;
import java.util.stream.Collectors;

import static net.twasiplugin.commands.CommandsPlugin.defaultPermission;
import static net.twasiplugin.commands.CommandsPlugin.prefix;

public class CommandsUserPlugin extends TwasiUserPlugin {

    @Override
    public void onInstall(TwasiInstallEvent e) {
        // Get the user we are installing for
        User user = getTwasiInterface().getStreamer().getUser();

        // Add the default permissionn
        user.getPermissions().add(defaultPermission);

        // Save everything
        Database.getStore().save(user);
        TwasiLogger.log.debug(prefix + " Commands installed successfully for " + getTwasiInterface().getStreamer().getUser().getTwitchAccount().getUserName());
    }

    @Override
    public void onUninstall(TwasiUninstallEvent e) {
        // Get the user we are uninstalling for
        User user = getTwasiInterface().getStreamer().getUser();

        List<Permissions> filteredPermissions = user.getPermissions().stream().filter(perm -> !perm.getName().equalsIgnoreCase("commands")).collect(Collectors.toList());

        // Set our (probably) removed permissions back to the user
        user.setPermissions(filteredPermissions);

        // Add the default permission
        user.getPermissions().add(defaultPermission);

        // Save everything
        Database.getStore().save(user);
        TwasiLogger.log.debug(prefix + " Commands uninstalled successfully for " + getTwasiInterface().getStreamer().getUser().getTwitchAccount().getUserName());
    }

    @Override
    public void onCommand(TwasiCommandEvent e) {
        User user = getTwasiInterface().getStreamer().getUser();
        TwasiCommand command = e.getCommand();

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
                    command.reply(getTranslation("commands.available", builder.toString()));
                }
            }
        }
    }

    @Override
    public void onMessage(TwasiMessageEvent e) {
        User user = getTwasiInterface().getStreamer().getUser();
        TwasiMessage msg = e.getMessage();

        String[] splitted = msg.getMessage().split(" ");
        String name = splitted[0];

        CustomCommand command = CommandStore.getCommandByName(user, name);

        if (command != null) {
            msg.reply(command.getContent());
        }
    }

}
