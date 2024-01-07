package eu.sengy.ecojobsbossbar.listeners;

import com.willfp.ecojobs.api.EcoJobsAPI;
import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent;
import com.willfp.ecojobs.jobs.Job;
import eu.sengy.ecojobsbossbar.EcoJobsBossbar;
import eu.sengy.ecojobsbossbar.api.config.Configs;
import eu.sengy.ecojobsbossbar.api.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EcoJobsListener implements Listener {

    EcoJobsBossbar pl;
    HashMap<String, Integer> cooldowns;

    HashMap<String, KeyedBossBar> bossBars = new HashMap<>();

    public EcoJobsListener(EcoJobsBossbar pl, HashMap<String, Integer> cooldowns){
        this.pl = pl;
        this.cooldowns = cooldowns;
    }

    @EventHandler
    public void onJobExp(PlayerJobExpGainEvent e) {

        Player p = e.getPlayer();
        List<String> disabledPlayers = new ArrayList<>(Configs.getConfig().getStringList("disabled"));

        if (!disabledPlayers.contains(p.getName())) {

            KeyedBossBar b = bossBars.get(p.getName());
            Job job = e.getJob();

            String name = job.getName();
            double amount = Math.round(e.getAmount() * 10.0) / 10.0;
            double xp = Math.round((EcoJobsAPI.getJobXP(p, job) + amount) * 10.0) / 10.0;
            double maxXp = Math.round(EcoJobsAPI.getJobXPRequired(p, job) * 10.0) / 10.0;
            int maxLevel = job.getMaxLevel();

            int level = EcoJobsAPI.getJobLevel(p, job);

            if (xp >= maxXp){
                level++;
                xp = xp - maxXp;
                maxXp = job.getExpForLevel(level);
            }

            double progress = xp / maxXp;

            if (progress > 1) progress = 1;

            cooldowns.put(p.getName(), Configs.getConfig().getInt("timeout"));
            b.setTitle(Colors.convertHex(Configs.getConfig().getString("progress")
                    .replace("%level%", String.valueOf(level))
                    .replace("%jobname%", name)
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%maxxp%", String.valueOf(maxXp))
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%maxlevel%", String.valueOf(maxLevel))
            ));
            b.setColor(BarColor.valueOf(Configs.getConfig().getString("color")));
            b.setStyle(BarStyle.valueOf(Configs.getConfig().getString("style")));
            b.setProgress(progress);
            b.setVisible(true);

            Bukkit.getScheduler().runTaskLater(pl, () -> {
                long cooldownTime = cooldowns.get(p.getName());

                if (cooldownTime <= 0) {
                    b.setVisible(false);
                }
            }, Configs.getConfig().getInt("timeout") * 20L);

        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!bossBars.containsKey(e.getPlayer().getName())) {
            KeyedBossBar b = Bukkit.createBossBar(
                    new NamespacedKey(pl, e.getPlayer().getName()),
                    "",
                    BarColor.valueOf(Configs.getConfig().getString("color")),
                    BarStyle.valueOf(Configs.getConfig().getString("style")));
            b.setVisible(false);
            b.addPlayer(e.getPlayer());
            bossBars.put(e.getPlayer().getName(), b);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Bukkit.removeBossBar(new NamespacedKey(pl, e.getPlayer().getName()));
        bossBars.remove(e.getPlayer().getName());
    }


}
