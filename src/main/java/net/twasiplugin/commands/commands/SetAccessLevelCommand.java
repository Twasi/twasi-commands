package net.twasiplugin.commands.commands;

import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasiplugin.commands.CommandAccessLevel;
import net.twasiplugin.commands.CommandRepository;
import net.twasiplugin.commands.CustomCommand;

public class SetAccessLevelCommand extends TwasiPluginCommand {

    private CommandRepository repo;

    public SetAccessLevelCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
        repo = DataService.get().get(CommandRepository.class);
    }

    @Override
    protected boolean execute(TwasiCustomCommandEvent e) {
        TranslationRenderer tr = e.getRenderer();
        try {
            CustomCommand cmd = repo.getCommandByName(e.getStreamer().getUser(), e.getArgs().get(0));

            if (cmd == null) {
                e.reply(tr.render("set-access-level.doesnt-exist"));
                return false;
            }

            tr.bindObject("command", cmd);

            if (cmd.getAccessLevel().equals(CommandAccessLevel.BROADCASTER)) {
                if (!e.hasPermission("commands.broadcaster.set-access")) {
                    e.reply(tr.render("set-access-level.broadcaster-only"));
                    return false;
                }
            }

            if (e.getArgs().size() == 1) {
                e.reply(tr.render("set-access-level.info"));
                return false;
            }

            CommandAccessLevel level = CommandAccessLevel.valueOf(e.getArgs().get(1).toUpperCase());

            cmd.setAccessLevel(level);
            repo.commit(cmd);

            tr.bindObject("level", level);

            e.reply(tr.render("set-access-level.success"));
            return false;
        } catch (Exception ignored) {
        }
        e.reply(tr.render("set-access-level.syntax"));
        return false;
    }

    @Override
    public String getCommandName() {
        return "setaccess";
    }

    @Override
    public String requirePermissionKey() {
        return "commands.mod.set-access";
    }
}
