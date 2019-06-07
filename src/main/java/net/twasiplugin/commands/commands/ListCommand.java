package net.twasiplugin.commands.commands;

import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.commands.database.CommandRepository;
import net.twasiplugin.commands.database.CustomCommand;

import java.util.List;

public class ListCommand extends TwasiPluginCommand {

    public ListCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    @Override
    public String getCommandName() {
        return "commands";
    }

    @Override
    public String requirePermissionKey() {
        return "commands.user.list";
    }

    @Override
    public boolean allowsTimer() {
        return true;
    }

    @Override
    public boolean allowsListing() {
        return false;
    }

    @Override
    protected boolean execute(TwasiCustomCommandEvent event) {
        List<CustomCommand> commands = ServiceRegistry.get(DataService.class).get(CommandRepository.class).getAllCommands(event.getStreamer().getUser());
        if (commands == null) {
            event.reply(event.getRenderer().render( "commands.noneAvailable"));
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
            event.reply(event.getRenderer().bind("commands", builder.toString()).render("commands.available"));
        }
        return true;
    }
}
