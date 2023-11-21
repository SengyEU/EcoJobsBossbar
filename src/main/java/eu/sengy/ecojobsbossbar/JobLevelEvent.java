package eu.sengy.ecojobsbossbar;

import com.willfp.ecojobs.api.EcoJobsAPI;
import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent;
import com.willfp.ecojobs.jobs.Job;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private EcoJobsBossbar pl;

    public JobLevelEvent(EcoJobsBossbar pl){
        this.pl = pl;
    }

    @EventHandler
    public void onJobExp(PlayerJobExpGainEvent e){

        Player p = e.getPlayer();

        BossBar b = bossBars.get(p.getName());

        Job job = e.getJob();

        String name = job.getName();
        double amount = e.getAmount();
        double xp = EcoJobsAPI.getJobXP(p,job)+amount;
        double maxXp = EcoJobsAPI.getJobXPRequired(p,job);
        int maxLevel = job.getMaxLevel();

        int level = EcoJobsAPI.getJobLevel(p,job);

        if(xp > maxXp){
            level++;
        }

        List<String> disabledPlayers = new ArrayList<>(pl.config.getStringList("disabled"));

        if(xp == maxXp){
            xp = 0;
            maxXp = job.getExpForLevel(level+1);
            level++;

            if(!disabledPlayers.contains(p.getName())){
                b.setTitle(MMesage.convertToString(pl.config.getString("progress_done")
                        .replace("%jobname%",name)
                        .replace("%maxlevel%",String.valueOf(maxLevel))
                ));
                b.setProgress(1);
                b.setVisible(true);
            }
        }

        double progress = xp/maxXp;

        if(level != job.getMaxLevel()){
            if(!disabledPlayers.contains(p.getName())){
                cooldowns.put(p.getName(), pl.config.getInt("timeout"));
                b.setTitle(MMesage.convertToString(pl.config.getString("progress")
                        .replace("%level%",String.valueOf(level))
                        .replace("%jobname%",name)
                        .replace("%xp%",String.valueOf(xp))
                        .replace("%maxxp%",String.valueOf(maxXp))
                        .replace("%amount%",String.valueOf(amount))
                        .replace("%maxlevel%",String.valueOf(maxLevel))
                ));
                b.setProgress(progress);
                b.setVisible(true);
            }
        }

        if(level != job.getMaxLevel()){
            Bukkit.getScheduler().runTaskLater(pl, () -> {
                long cooldownTime = cooldowns.get(p.getName());

                if (cooldownTime <= 0) {
                    b.setVisible(false);
                }
            }, pl.config.getInt("timeout") * 20L);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(!bossBars.containsKey(e.getPlayer().getName())){
            BossBar b = Bukkit.createBossBar("",color(),style());
            b.setVisible(false);
            b.addPlayer(e.getPlayer());
            bossBars.put(e.getPlayer().getName(),b);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        bossBars.remove(e.getPlayer().getName());
    }

    private BarStyle style(){
        if(pl.config.getString("style").equalsIgnoreCase("SEGMENTED_20")) return BarStyle.SEGMENTED_20;
        if(pl.config.getString("style").equalsIgnoreCase("SEGMENTED_12")) return BarStyle.SEGMENTED_12;
        if(pl.config.getString("style").equalsIgnoreCase("SEGMENTED_10")) return BarStyle.SEGMENTED_10;
        if(pl.config.getString("style").equalsIgnoreCase("SEGMENTED_6")) return BarStyle.SEGMENTED_6;
        return BarStyle.SOLID;
    }

    private BarColor color(){
        if(pl.config.getString("color").equalsIgnoreCase("BLUE")) return BarColor.BLUE;
        if(pl.config.getString("color").equalsIgnoreCase("GREEN")) return BarColor.GREEN;
        if(pl.config.getString("color").equalsIgnoreCase("PINK")) return BarColor.PINK;
        if(pl.config.getString("color").equalsIgnoreCase("PURPLE")) return BarColor.PURPLE;
        if(pl.config.getString("color").equalsIgnoreCase("RED")) return BarColor.RED;
        if(pl.config.getString("color").equalsIgnoreCase("YELLOW")) return BarColor.YELLOW;
        return BarColor.WHITE;

    }

}