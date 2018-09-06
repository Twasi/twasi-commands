package net.twasiplugin.commands.web;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import net.twasi.core.database.models.User;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.services.providers.JWTService;
import net.twasiplugin.commands.CommandRepository;
import net.twasiplugin.commands.CustomCommand;
import net.twasiplugin.commands.web.model.CommandDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CommandResolver implements GraphQLQueryResolver {
    private CommandRepository repo = ServiceRegistry.get(DataService.class).get(CommandRepository.class);

    public List<CommandDTO> getCommands(String token) {
        try {
            User user = ServiceRegistry.get(JWTService.class).getManager().getUserFromToken(token);
            try {
                ServiceRegistry.get(DataService.class).get(net.twasi.core.database.repositories.UserRepository.class).commit(user);
            } catch (IllegalArgumentException ignored) {}

            if (user == null) {
                return null;
            }

            List<CustomCommand> commands = repo.getAllCommands(user);

            return commands.stream()
                    .map(cmd -> new CommandDTO(user, cmd.getId().toString(), cmd.getName(), cmd.getContent()))
                    .collect(Collectors.toList());
        } catch (Throwable t) {
            TwasiLogger.log.error("Fatal Error in GraphQL.", t);
            return null;
        }
    }

}
