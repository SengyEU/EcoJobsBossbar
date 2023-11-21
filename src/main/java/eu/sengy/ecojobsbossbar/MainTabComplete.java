package eu.sengy.ecojobsbossbar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("toggle");

            if(sender.hasPermission("ecojobsbossbar.reload")){
                arguments.add("reload");
            }

            return arguments;
        }
        else{
            return new ArrayList<>();
        }

    }

}
