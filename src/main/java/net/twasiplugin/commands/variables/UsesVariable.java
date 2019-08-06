package net.twasiplugin.commands.variables;

import net.twasi.core.interfaces.api.TwasiInterface;
import net.twasi.core.models.Message.TwasiMessage;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.TwasiVariable;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.commands.database.CommandRepository;
import net.twasiplugin.commands.database.CustomCommand;

import java.util.Collections;
import java.util.List;

public class UsesVariable extends TwasiVariable {
    public UsesVariable(TwasiUserPlugin owner) {
        super(owner);
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList("uses");
    }

    @Override
    public String process(String name, TwasiInterface inf, String[] params, TwasiMessage msg) {
        // Check which commands was executed

        String[] splitted = msg.getMessage().split(" ");
        String commandName = splitted[0];

        CustomCommand command = ServiceRegistry.get(DataService.class).get(CommandRepository.class).getCommandByName(inf.getStreamer().getUser(), commandName);

        if (command != null) {
            // It was a commands that was executed! Insert uses.
            return String.valueOf(command.getUses());
        } else {
            // Uses was used in anything other than a commands. Return error message.
            return "NOT_IN_COMMAND";
        }
    }
}
