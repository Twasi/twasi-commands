package net.twasiplugin.commands;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import net.twasi.core.plugin.TwasiPlugin;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DatabaseService;
import net.twasiplugin.commands.database.CustomCommand;
import net.twasiplugin.commands.web.CommandResolver;

public class CommandsPlugin extends TwasiPlugin {
    static String prefix = "[COMMANDS] ";

    public CommandsPlugin() {
    }

    @Override
    public void onActivate() {
        // Register our own entity
        ServiceRegistry.get(DatabaseService.class).getMorphia().mapPackageFromClass(CustomCommand.class);

        // Register our own api endpoint
        // registerRoute("/api/commands", new CommandHandler());

        TwasiLogger.log.info(prefix + "activated!");
    }

    @Override
    public void onDeactivate() {
        TwasiLogger.log.info(prefix + "deactivated");
    }

    @Override
    public Class<? extends TwasiUserPlugin> getUserPluginClass() {
        return CommandsUserPlugin.class;
    }

    @Override
    public GraphQLQueryResolver getGraphQLResolver() {
        return new CommandResolver();
    }
}
