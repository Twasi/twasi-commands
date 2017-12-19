package net.twasi.commands.web;

import com.sun.net.httpserver.HttpExchange;
import net.twasi.commands.CommandStore;
import net.twasi.commands.CustomCommand;
import net.twasi.core.database.models.User;
import net.twasi.core.webinterface.dto.ApiDTO;
import net.twasi.core.webinterface.dto.error.UnauthorizedDTO;
import net.twasi.core.webinterface.lib.Commons;
import net.twasi.core.webinterface.lib.RequestHandler;

import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler extends RequestHandler {

    @Override
    public void handleGet(HttpExchange t) {
        if (!isAuthenticated(t)) {
            Commons.writeDTO(t, new UnauthorizedDTO(), 403);
        }

        User user = getUser(t);

        List<CustomCommand> commands = CommandStore.getAllCommands(user);
        System.out.println(commands.toString());

        CommandResponseDTO dto = new CommandResponseDTO(commands);
        Commons.writeDTO(t, dto, 200);
    }

    class CommandResponseDTO extends ApiDTO {
        public List<CustomCommandDTO> commands;

        public CommandResponseDTO(List<CustomCommand> commands) {
            super(true);
            this.commands = commands.stream()
                    .map(command ->
                            new CustomCommandDTO(command.getName(), command.getContent())
                    ).collect(Collectors.toList());
        }

        class CustomCommandDTO {
            String name;
            String content;

            CustomCommandDTO(String name, String content) {
                this.name = name;
                this.content = content;
            }
        }
    }
}
