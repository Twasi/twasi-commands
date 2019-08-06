package net.twasiplugin.commands.commands;

import net.twasi.core.database.models.User;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasiplugin.commands.database.CommandRepository;

import java.time.Duration;

public class DelCommand extends TwasiPluginCommand {

    public DelCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    @Override
    public String getCommandName() {
        return "del";
    }

    @Override
    public String requirePermissionKey() {
        return "commands.mod.delete";
    }

    @Override
    public boolean allowsTimer() {
        return false;
    }

    @Override
    public boolean allowsListing() {
        return false;
    }

    @Override
    public Duration getCooldown() {
        return Duration.ZERO;
    }

    @Override
    protected boolean execute(TwasiCustomCommandEvent event) {
        // Check length
        if (event.getArgs().size() < 1) {
            // Reply with instructions
            event.reply(event.getRenderer().render("delete.usage"));
            return false;
        }

        User user = event.getStreamer().getUser();
        String name = event.getArgs().get(0);
        TranslationRenderer renderer = event.getRenderer().bind("name", name);

        // If the commands doesn't exist
        if (ServiceRegistry.get(DataService.class).get(CommandRepository.class).deleteCommandByName(user, name)) {
            // Reply to the user
            event.reply(renderer.render( "delete.successful"));
        } else {
            event.reply(renderer.render("delete.doesntExist"));
        }
        return true;
    }
}
