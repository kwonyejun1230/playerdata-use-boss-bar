package me.yourname.bossbarinfo;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class BossBarInfo extends JavaPlugin {

    private LuckPerms luckPerms;
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            getLogger().warning("LuckPerms API를 찾을 수 없습니다. LuckPerms 플러그인이 필요합니다.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();

                    // 플레이어 그룹 가져오기 (LuckPerms)
                    String group = "Unknown";
                    User user = luckPerms.getUserManager().getUser(uuid);
                    if (user != null) {
                        QueryOptions queryOptions = luckPerms.getContextManager()
                            .getQueryOptions(user)
                            .orElse(QueryOptions.defaultContextualOptions());
                        group = user.getPrimaryGroup(queryOptions);
                    }

                    String coords = String.format("X: %.1f Y: %.1f Z: %.1f",
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ());

                    String title = String.format("%s (%s) | %s | Group: %s",
                        player.getName(),
                        uuid.toString().substring(0,8),
                        coords,
                        group);

                    BossBar bar = bossBars.computeIfAbsent(uuid, id -> 
                        Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID));

                    bar.setTitle(title);
                    bar.setProgress(1.0);
                    bar.setVisible(true);
                    bar.addPlayer(player);
                }
            }
        }.runTaskTimer(this, 0L, 10L); // 2초마다 갱신
    }

    @Override
    public void onDisable() {
        bossBars.values().forEach(bar -> {
            bar.removeAll();
            bar.setVisible(false);
        });
        bossBars.clear();
    }
}
