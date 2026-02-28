package me.tiger.biomeChange;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BiomeChange extends JavaPlugin {

    public static final long INTERVAL_TICKS = 20L * 180L; // 3 minutes
    public static final int RADIUS_CHUNKS = 4;

    private final ConcurrentHashMap<UUID, BukkitTask> active = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        getCommand("biomechange").setExecutor(new BiomeChangeCommand(this));
        getLogger().info("BiomeChange enabled.");
    }

    @Override
    public void onDisable() {
        active.values().forEach(BukkitTask::cancel);
        active.clear();
    }

    public void enableFor(Player player) {
        disableFor(player);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                this,
                () -> BiomeSwitcher.switchBiomeAround(player, RADIUS_CHUNKS),
                40L,
                INTERVAL_TICKS
        );

        active.put(player.getUniqueId(), task);
    }

    public void disableFor(Player player) {
        BukkitTask task = active.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    public boolean isEnabledFor(Player player) {
        return active.containsKey(player.getUniqueId());
    }
}