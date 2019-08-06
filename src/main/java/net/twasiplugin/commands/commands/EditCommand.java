package net.twasiplugin.commands.commands;

import net.twasi.core.database.models.User;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasiplugin.commands.database.CommandRepository;
import net.twasiplugin.commands.database.CustomCommand;

import java.time.Duration;

public class EditCommand extends TwasiPluginCommand {

    public EditCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    @Override
    public String getCommandName() {
        return "edit";
    }

    @Override
    public String requirePermissionKey() {
        return "commands.mod.edit";
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
        if (event.getArgs().size() < 3) {
            // Reply with instructions
            event.reply(event.getRenderer().render("edit.usage"));
            return false;
        }

        String[] splitted = event.getArgsAsOne().split(" ", 2);

        // Map to our strings
        String name = splitted[0];
        String content = splitted[1];

        User user = event.getStreamer().getUser();
        TranslationRenderer renderer = event.getRenderer().bind("name", name);

        // We can't change cooldown of commands in chat so just don't change it.
        CustomCommand customCommand = ServiceRegistry.get(DataService.class).get(CommandRepository.class).getCommandByName(user, name);

        // If the commands doesn't exist notify the user
        if (ServiceRegistry.get(DataService.class).get(CommandRepository.class).editCommandByName(user, name, content, customCommand.getCooldown(), customCommand.getAccessLevel())) {
            // Reply to the user
            event.reply(renderer.render("edit.successful"));
        } else {
            event.reply(renderer.render("edit.doesntExist"));
        }
        return true;
    }
}
