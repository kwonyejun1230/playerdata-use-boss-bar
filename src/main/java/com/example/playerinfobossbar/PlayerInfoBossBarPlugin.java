package com.example.playerinfobossbar;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PlayerInfoBossBarPlugin extends JavaPlugin {

    private Permission perms;

    @Override
    public void onEnable() {
        if (!setupPermissions()) {
            getLogger().warning("Vault + LuckPerms not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String group = perms.getPrimaryGroup(player);
                    Location loc = player.getLocation();
                    String barText = String.format("§b%s §7| UUID: §f%s §7| §aX: %.1f Y: %.1f Z: %.1f §7| Group: §e%s",
                            player.getName(), player.getUniqueId().toString().substring(0, 8),
                            loc.getX(), loc.getY(), loc.getZ(), group);
                    BossBar bar = Bukkit.createBossBar(barText, BarColor.BLUE, BarStyle.SEGMENTED_10);
                    bar.setProgress(1.0);
                    bar.addPlayer(player);
                    bar.setVisible(true);

                    // Hide after a while
                    Bukkit.getScheduler().runTaskLater(PlayerInfoBossBarPlugin.this, () -> {
                        bar.removeAll();
                    }, 40L);
                }
            }
        }.runTaskTimer(this, 0L, 60L);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
