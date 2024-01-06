package eu.sengy.ecojobsbossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class EcoJobsBossbar extends JavaPlugin {
    public static HashMap<String, BossBar> bossBars = new HashMap<>();

    public static HashMap<String, Integer> cooldowns = new HashMap<>();

    FileConfiguration config = this.getConfig();


    @Override
    public void onEnable() {

        saveDefaultConfig();

        getCommand("ecojobsbossbar").setExecutor(new MainCommand(this));

        getServer().getPluginManager().registerEvents(new JobLevelEvent(this), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> cooldowns.replaceAll((k, v) -> v - 1), 0, 20);
    }

    @Override
    public void onDisable() {

    }
}
