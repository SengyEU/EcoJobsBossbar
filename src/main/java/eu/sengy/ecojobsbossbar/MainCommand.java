package eu.sengy.ecojobsbossbar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MainCommand implements CommandExecutor {

    private EcoJobsBossbar pl;

    public MainCommand(EcoJobsBossbar pl){
        this.pl = pl;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                if(sender.hasPermission("ecojobsbossbar.reload")){
                    pl.reloadConfig();
                    pl.config = pl.getConfig();
                    sender.sendMessage(MMesage.convertToString(pl.config.getString("reload_message")));
                }
                else {
                    sender.sendMessage(MMesage.convertToString(pl.config.getString("noperm")));
                }
            }
            else if(args[0].equalsIgnoreCase("toggle")){
                List<String> disabledPlayers = pl.config.getStringList("disabled");

                if(!disabledPlayers.contains(sender.getName())){
                    disabledPlayers.add(sender.getName());
                    sender.sendMessage(MMesage.convertToString(pl.config.getString("toggle_no")));
                }
                else {
                    disabledPlayers.remove(sender.getName());
                    sender.sendMessage(MMesage.convertToString(pl.config.getString("toggle_yes")));
                }
                pl.getConfig().set("disabled",disabledPlayers);
                pl.saveConfig();
            }
            else {
                sender.sendMessage(MMesage.convertToString(pl.config.getString("unknown_subcommand")));
            }
        }

        return true;
    }
}
