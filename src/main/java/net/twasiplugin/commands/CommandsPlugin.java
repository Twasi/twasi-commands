package net.twasiplugin.commands;

import net.twasi.core.plugin.TwasiPlugin;
import net.twasi.core.database.Database;
import net.twasi.core.database.models.permissions.PermissionEntity;
import net.twasi.core.database.models.permissions.PermissionEntityType;
import net.twasi.core.database.models.permissions.PermissionGroups;
import net.twasi.core.database.models.permissions.Permissions;
import net.twasi.core.logger.TwasiLogger;
import net.twasiplugin.commands.web.CommandHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandsPlugin extends TwasiPlugin {

    static List<String> permissionKeys = Arrays.asList("commands.add", "commands.edit", "commands.delete", "commands.list");

    static Permissions defaultPermission = new Permissions(
            Collections.singletonList(
                    new PermissionEntity(
                            PermissionEntityType.GROUP,
                            PermissionGroups.MODERATOR,
                            null
                    )
            ),
            Arrays.asList("commands.add", "commands.edit", "commands.delete", "commands.list"),
            "commands"
    );

    static String prefix = "[COMMANDS] ";

    public CommandsPlugin() {
    }

    @Override
    public void onActivate() {
        // Register our own entity
        Database.getMorphia().mapPackageFromClass(CustomCommand.class);

        // Register our own api endpoint
        registerRoute("/api/commands", new CommandHandler());

        TwasiLogger.log.info(prefix + "activated!");
    }

    @Override
    public void onDeactivate() {
        TwasiLogger.log.info(prefix + "deactivated");
    }

    @Override
    public Class getUserPluginClass() {
        return CommandsUserPlugin.class;
    }
}
