package eu.sengy.ecojobsbossbar.commands;

import eu.sengy.ecojobsbossbar.EcoJobsBossbar;
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

    private final EcoJobsBossbar pl;

    public MainCommand(EcoJobsBossbar pl){
        this.pl = pl;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("toggle");

            if (sender.hasPermission("ecojobsbossbar.reload")) {
                arguments.add("reload");
            }

            return arguments;
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("ecojobsbossbar.reload")) {
                    try {
                        Configs.reloadConfig();
                        Configs.reloadMessages();
                    } catch (InvalidConfigurationException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("reload_message")));
                } else {
                    sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("noperm")));
                }
            } else if (args[0].equalsIgnoreCase("toggle")) {
                List<String> disabledPlayers = Configs.getMessages().getStringList("disabled");

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
            } else {
                sender.sendMessage(Colors.convertHex(Configs.getMessages().getString("unknown_subcommand")));
            }
        }

        return true;
    }
}
