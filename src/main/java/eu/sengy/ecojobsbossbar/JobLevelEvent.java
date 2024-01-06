package eu.sengy.ecojobsbossbar;

import com.willfp.ecojobs.api.EcoJobsAPI;
import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent;
import com.willfp.ecojobs.jobs.Job;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

import static eu.sengy.ecojobsbossbar.EcoJobsBossbar.bossBars;
import static eu.sengy.ecojobsbossbar.EcoJobsBossbar.cooldowns;

public class JobLevelEvent implements Listener {

    private final EcoJobsBossbar pl;

    public JobLevelEvent(EcoJobsBossbar pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onJobExp(PlayerJobExpGainEvent e) {

        Player p = e.getPlayer();
        List<String> disabledPlayers = new ArrayList<>(pl.config.getStringList("disabled"));

        if (!disabledPlayers.contains(p.getName())) {

            BossBar b = bossBars.get(p.getName());

            Job job = e.getJob();

            String name = job.getName();
            double amount = e.getAmount();
            double xp = EcoJobsAPI.getJobXP(p, job) + amount;
            double maxXp = EcoJobsAPI.getJobXPRequired(p, job);
            int maxLevel = job.getMaxLevel();

            int level = EcoJobsAPI.getJobLevel(p, job);

            if (xp > maxXp) {
                level++;
            }

            if (xp == maxXp) {
                xp = 0;
                maxXp = job.getExpForLevel(level + 1);
                level++;

                b.setTitle(Colors.convertHex(pl.config.getString("progress_done")
                        .replace("%jobname%", name)
                        .replace("%maxlevel%", String.valueOf(maxLevel))
                ));
                b.setProgress(1);
                b.setVisible(true);
            }

            double progress = xp / maxXp;

            if (progress > 1) {
                progress = 1;
            }

            if (level != job.getMaxLevel()) {
                cooldowns.put(p.getName(), pl.config.getInt("timeout"));
                b.setTitle(Colors.convertHex(pl.config.getString("progress")
                        .replace("%level%", String.valueOf(level))
                        .replace("%jobname%", name)
                        .replace("%xp%", String.valueOf(Math.round(xp * 10.0) / 10.0))
                        .replace("%maxxp%", String.valueOf(Math.round(maxXp * 10.0) / 10.0))
                        .replace("%amount%", String.valueOf(Math.round(amount * 10.0) / 10.0))
                        .replace("%maxlevel%", String.valueOf(maxLevel))
                ));
                b.setColor(BarColor.valueOf(pl.config.getString("color")));
                b.setStyle(BarStyle.valueOf(pl.config.getString("style")));
                b.setProgress(progress);
                b.setVisible(true);
            }

            if (level != job.getMaxLevel()) {
                Bukkit.getScheduler().runTaskLater(pl, () -> {
                    long cooldownTime = cooldowns.get(p.getName());

                    if (cooldownTime <= 0) {
                        b.setVisible(false);
                    }
                }, pl.config.getInt("timeout") * 20L);
            }

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!bossBars.containsKey(e.getPlayer().getName())) {
            BossBar b = Bukkit.createBossBar("", BarColor.valueOf(pl.config.getString("color")), BarStyle.valueOf(pl.config.getString("style")));
            b.setVisible(false);
            b.addPlayer(e.getPlayer());
            bossBars.put(e.getPlayer().getName(), b);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        bossBars.remove(e.getPlayer().getName());
    }

}