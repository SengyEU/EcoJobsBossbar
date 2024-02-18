package eu.sengy.ecojobsbossbar.commands;

import eu.sengy.ecojobsbossbar.api.config.Configs;
import eu.sengy.ecojobsbossbar.api.utils.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainCommand implements TabExecutor {

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("toggle");

            if (s.hasPermission("ecojobsbossbar.reload")) {
                arguments.add("reload");
            }

            return arguments;
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1){
            sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("unknown_subcommand")));
            return false;
        };
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("ecojobsbossbar.reload")) {
                    sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("noperm")));
                    return false;
                }
                try {
                    Configs.reloadConfig();
                    Configs.reloadMessages();
                } catch (InvalidConfigurationException | IOException e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("reload_message")));
            }
            case "toggle" -> {
                List<String> disabledPlayers = Configs.getConfig().getStringList("disabled");

                if (!disabledPlayers.contains(sender.getName())) {
                    disabledPlayers.add(sender.getName());
                    sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("toggle_no")));
                } else {
                    disabledPlayers.remove(sender.getName());
                    sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("toggle_yes")));
                }
                Configs.getConfig().set("disabled", disabledPlayers);
                try {
                    Configs.saveConfig();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("unknown_subcommand")));
        }

        return true;
    }
}
