package net.twasiplugin.commands;

import net.twasi.core.database.models.User;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.models.Message.TwasiCommand;
import net.twasi.core.models.Message.TwasiMessage;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.events.TwasiCommandEvent;
import net.twasi.core.plugin.api.events.TwasiInstallEvent;
import net.twasi.core.plugin.api.events.TwasiMessageEvent;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;

import java.util.List;

import static net.twasiplugin.commands.CommandsPlugin.prefix;

public class CommandsUserPlugin extends TwasiUserPlugin {

    /**
     * Installs the plugin, adds the default permissions.
     * @param e A TwasiInstallEvent
     */
    @Override
    public void onInstall(TwasiInstallEvent e) {
        e.getDefaultGroup().addKey("commands.user.*");
        e.getModeratorsGroup().addKey("commands.mod.*");

        TwasiLogger.log.debug(prefix + " Commands installed successfully for " + getTwasiInterface().getStreamer().getUser().getTwitchAccount().getUserName());
    }

    /**
     * Uninstalls the plugin, removes all permissions objects with the name "commands".
     * @param e A TwasiUninstallEvent
     */
    @Override
    public void onUninstall(TwasiInstallEvent e) {
        e.getDefaultGroup().removeKey("commands.user.*");
        e.getModeratorsGroup().removeKey("commands.mod.*");

        TwasiLogger.log.debug(prefix + " Commands uninstalled successfully for " + getTwasiInterface().getStreamer().getUser().getTwitchAccount().getUserName());
    }

    /**
     * Handles a command event.
     * This handles command creation, editing and deletion, and can also provide a list of all commands.
     * @param e A TwasiCommandEvent
     */
    @Override
    public void onCommand(TwasiCommandEvent e) {
        User user = getTwasiInterface().getStreamer().getUser();
        TwasiCommand command = e.getCommand();

        // If the command is add
        if (command.getCommandName().equalsIgnoreCase("add")) {
            if (user.hasPermission(command.getSender(), "commands.mod.add")) {
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
                if (ServiceRegistry.get(DataService.class).get(CommandRepository.class).createCommand(user, name, content) == null) {
                    // Reply to the user
                    command.reply(getTranslations().getTranslation(user, "add.alreadyExist", name));
                } else {
                    command.reply(getTranslations().getTranslation(user, "add.successful", name));
                }
            }
        }

        // If the command is edit
        if (command.getCommandName().equalsIgnoreCase("edit")) {
            if (user.hasPermission(command.getSender(), "commands.mod.edit")) {
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
                if (ServiceRegistry.get(DataService.class).get(CommandRepository.class).editCommandByName(user, name, content)) {
                    // Reply to the user
                    command.reply(getTranslations().getTranslation(user, "edit.successful", name));
                } else {
                    command.reply(getTranslations().getTranslation(user, "edit.doesntExist", name));
                }
            }
        }

        // If the command is delete
        if (command.getCommandName().equalsIgnoreCase("delete")) {
            if (user.hasPermission(command.getSender(), "commands.mod.delete")) {
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
                if (ServiceRegistry.get(DataService.class).get(CommandRepository.class).deleteCommandByName(user, name)) {
                    // Reply to the user
                    command.reply(getTranslations().getTranslation(user, "delete.successful", name));
                } else {
                    command.reply(getTranslations().getTranslation(user, "delete.doesntExist", name));
                }
            }
        }

        if (command.getCommandName().equalsIgnoreCase("commands")) {
            if (user.hasPermission(command.getSender(), "commands.user.list")) {
                List<CustomCommand> commands = ServiceRegistry.get(DataService.class).get(CommandRepository.class).getAllCommands(user);
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

    /**
     * Handles the execution of custom commands.
     * @param e A TwasiMessageEvent
     */
    @Override
    public void onMessage(TwasiMessageEvent e) {
        User user = getTwasiInterface().getStreamer().getUser();
        TwasiMessage msg = e.getMessage();

        String[] splitted = msg.getMessage().split(" ");
        String name = splitted[0];

        CustomCommand command = ServiceRegistry.get(DataService.class).get(CommandRepository.class).getCommandByName(user, name);

        if (command != null) {
            msg.reply(command.getContent());
        }
    }

}
