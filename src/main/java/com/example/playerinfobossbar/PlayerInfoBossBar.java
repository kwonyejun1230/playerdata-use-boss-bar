package com.example.playerinfobossbar;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInfoActionBarPlugin extends JavaPlugin {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        luckPerms = LuckPermsProvider.get();
        getLogger().info("플러그인 활성화됨: ActionBar 표시");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendActionBarInfo(player);
                }
            }
        }.runTaskTimer(this, 0L, 40L); // 2초마다 업데이트
    }

    private void sendActionBarInfo(Player player) {
        String uuid = player.getUniqueId().toString().substring(0, 8);
        String name = player.getName();
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        String group = "unknown";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            group = user.getPrimaryGroup();
        }

        String message = String.format("§b%s §7| §fUUID: %s §7| §a%d %d %d §7| §e%s",
                name, uuid, x, y, z, group
        );

        player.sendActionBar(message);
    }
}
