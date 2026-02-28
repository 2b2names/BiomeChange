package me.tiger.biomeChange;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BiomeSwitcher {

    // Pick biomes that look VERY different (good for silent videos)
    private static final List<Biome> BIOMES = List.of(
            Biome.DESERT,
            Biome.SNOWY_PLAINS,
            Biome.JUNGLE,
            Biome.SWAMP,
            Biome.BADLANDS,
            Biome.SAVANNA,
            Biome.DARK_FOREST,
            Biome.OCEAN,
            Biome.MUSHROOM_FIELDS
    );

    public static void switchBiomeAround(Player player, int radiusChunks) {
        if (player == null || !player.isOnline()) return;

        World world = player.getWorld();
        Biome chosen = BIOMES.get(ThreadLocalRandom.current().nextInt(BIOMES.size()));

        int centerX = player.getLocation().getBlockX();
        int centerZ = player.getLocation().getBlockZ();

        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        int radiusBlocks = radiusChunks * 16;

        // Ensure nearby chunks are loaded so biome changes take effect cleanly
        int centerChunkX = centerX >> 4;
        int centerChunkZ = centerZ >> 4;
        for (int cx = centerChunkX - radiusChunks; cx <= centerChunkX + radiusChunks; cx++) {
            for (int cz = centerChunkZ - radiusChunks; cz <= centerChunkZ + radiusChunks; cz++) {
                if (!world.isChunkLoaded(cx, cz)) {
                    world.getChunkAt(cx, cz); // loads chunk
                }
            }
        }

        // Paper 1.21+ compatible biome setting (Chunk#setBiome removed)
        for (int x = centerX - radiusBlocks; x <= centerX + radiusBlocks; x++) {
            for (int z = centerZ - radiusBlocks; z <= centerZ + radiusBlocks; z++) {
                for (int y = minY; y < maxY; y++) {
                    world.setBiome(x, y, z, chosen);
                }
            }
        }

        // Refresh chunks around player so the client updates colors/weather fast
        for (int cx = centerChunkX - radiusChunks; cx <= centerChunkX + radiusChunks; cx++) {
            for (int cz = centerChunkZ - radiusChunks; cz <= centerChunkZ + radiusChunks; cz++) {
                world.refreshChunk(cx, cz);
            }
        }

        // Big “moment” for no-voice content
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        player.sendTitle(
                ChatColor.AQUA + "BIOME SHIFT!",
                ChatColor.WHITE + formatBiomeName(chosen),
                10, 50, 10
        );

        Bukkit.getLogger().info("[BiomeChange] " + player.getName() + " -> " + chosen.name());
    }

    private static String formatBiomeName(Biome biome) {
        String name = biome.name().toLowerCase().replace("_", " ");
        String[] parts = name.split(" ");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) continue;
            result.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }
        return result.toString().trim();
    }
}