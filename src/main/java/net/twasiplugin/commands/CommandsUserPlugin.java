package net.twasiplugin.commands;

import net.twasi.core.database.models.User;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.models.Message.TwasiMessage;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.events.TwasiEnableEvent;
import net.twasi.core.plugin.api.events.TwasiInstallEvent;
import net.twasi.core.plugin.api.events.TwasiMessageEvent;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.commands.commands.AddCommand;
import net.twasiplugin.commands.commands.DelCommand;
import net.twasiplugin.commands.commands.EditCommand;
import net.twasiplugin.commands.commands.ListCommand;
import net.twasiplugin.commands.database.CommandRepository;
import net.twasiplugin.commands.database.CustomCommand;
import net.twasiplugin.commands.variables.UsesVariable;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static net.twasiplugin.commands.CommandsPlugin.prefix;

public class CommandsUserPlugin extends TwasiUserPlugin {

    private Map<ObjectId, Date> cooldowns = new HashMap<>();

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

    @Override
    public void onEnable(TwasiEnableEvent e) {
        registerCommand(AddCommand.class);
        registerCommand(DelCommand.class);
        registerCommand(EditCommand.class);
        registerCommand(ListCommand.class);

        registerVariable(UsesVariable.class);
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
            // Is it on cooldown?
            if (cooldowns.containsKey(command.getId())) {
                // if the earliest next use is in the future, skip it. It's on cooldown.
                if (cooldowns.get(command.getId()).after(new Date())) {
                    return;
                }
            }

            msg.reply(command.getContent());

            // increment uses
            command.setUses(command.getUses() + 1);

            // apply cooldown, if any
            if (command.getCooldown() != 0) {
                Date now = new Date();
                Date earliestNextUse = new Date(now.getTime() + command.getCooldown() * 1000);
                cooldowns.put(command.getId(), earliestNextUse);
            }

            ServiceRegistry.get(DataService.class).get(CommandRepository.class).commit(command);
        }
    }
}
