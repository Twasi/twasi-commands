package net.twasiplugin.commands.web.model;

import net.twasi.core.database.models.User;
import net.twasiplugin.commands.CommandRepository;
import net.twasiplugin.commands.CustomCommand;

import java.util.List;
import java.util.stream.Collectors;

public class CommandPluginDTO {
    private CommandRepository repo;
    private User user;

    public CommandPluginDTO(CommandRepository repo, User user) {
        this.repo = repo;
        this.user = user;
    }

    public List<CommandDTO> commands() {
        return repo.getAllCommands(user).stream().map(cmd -> new CommandDTO(user, cmd)).collect(Collectors.toList());
    }

    public CommandDTO update(String id, String name, String content) {
        if (repo.editCommand(user, id, name, content)) {
            return new CommandDTO(user, repo.getCommandById(user, id));
        }
        return null;
    }

    public CommandDTO single(String id) {
        return new CommandDTO(user, repo.getCommandById(user, id));
    }

    public CommandDTO delete(String id) {
        CustomCommand command = repo.getCommandById(user, id);

        if (repo.deleteCommand(user, id)) {
            return new CommandDTO(user, command);
        }
        return null;
    }

    public CommandDTO create(String name, String content) {
        String id = repo.createCommand(user, name, content);

        if (id == null) {
            return null;
        }

        return new CommandDTO(user, repo.getCommandById(user, id));
    }

}