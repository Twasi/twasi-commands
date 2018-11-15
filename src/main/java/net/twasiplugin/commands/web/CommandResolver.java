package net.twasiplugin.commands.web;

import net.twasi.core.database.models.User;
import net.twasi.core.graphql.TwasiCustomResolver;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.commands.CommandRepository;
import net.twasiplugin.commands.web.model.CommandPluginDTO;

public class CommandResolver extends TwasiCustomResolver {
    private CommandRepository repo = ServiceRegistry.get(DataService.class).get(CommandRepository.class);

    public CommandPluginDTO getCommands(String token) {
        User user = getUser(token);

        if (user == null) {
            return null;
        }

        if (!user.getInstalledPlugins().contains("Commands")) {
            return null;
        }

        return new CommandPluginDTO(repo, user);
    }

}
