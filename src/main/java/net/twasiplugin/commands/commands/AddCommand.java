package net.twasiplugin.commands.commands;

import net.twasi.core.database.models.User;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasiplugin.commands.CommandAccessLevel;
import net.twasiplugin.commands.database.CommandRepository;

import java.time.Duration;

public class AddCommand extends TwasiPluginCommand {

    public AddCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    @Override
    public String getCommandName() {
        return "add";
    }

    @Override
    public String requirePermissionKey() {
        return "commands.mod.add";
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
        if (event.getArgs().size() < 2) {
            // Reply with instructions
            event.reply(event.getRenderer().render("add.usage"));
            return false;
        }

        String[] splitted = event.getArgsAsOne().split(" ", 2);

        // Map to our strings
        String name = splitted[0];
        String content = splitted[1];

        User user = event.getStreamer().getUser();
        TranslationRenderer renderer = event.getRenderer().bind("name", name);

        // If the commands already exists notify the user
        if (ServiceRegistry.get(DataService.class).get(CommandRepository.class).createCommand(user, name, content, 0, CommandAccessLevel.VIEWER) == null) {
            // Reply to the user
            event.reply(renderer.render("add.alreadyExist"));
        } else {
            event.reply(renderer.render("add.successful"));
        }
        return true;
    }
}
