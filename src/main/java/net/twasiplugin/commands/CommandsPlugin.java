package net.twasiplugin.commands;

import net.twasi.core.plugin.TwasiPlugin;
import net.twasi.core.database.models.permissions.PermissionEntity;
import net.twasi.core.database.models.permissions.PermissionEntityType;
import net.twasi.core.database.models.permissions.PermissionGroups;
import net.twasi.core.database.models.permissions.Permissions;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DatabaseService;

import java.util.Arrays;
import java.util.Collections;

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
}
