package eu.sengy.ecojobsbossbar;

import eu.sengy.ecojobsbossbar.api.config.Configs;
import eu.sengy.ecojobsbossbar.commands.MainCommand;
import eu.sengy.ecojobsbossbar.listeners.EcoJobsListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class EcoJobsBossbar extends JavaPlugin {

    HashMap<String, Integer> cooldowns = new HashMap<>();


    @Override
    public void onEnable() {

        Configs.init(this);
        getServer().getPluginManager().registerEvents(new EcoJobsListener(this, cooldowns), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> cooldowns.replaceAll((k, v) -> v - 1), 0, 20);

        getCommand("ecojobsbossbar").setExecutor(new MainCommand());

    }

    @Override
    public void onDisable() {

    }
}
