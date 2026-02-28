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
        if (!player.isOnline()) return;

        World world = player.getWorld();
        Biome chosen = BIOMES.get(ThreadLocalRandom.current().nextInt(BIOMES.size()));

        int centerChunkX = player.getLocation().getBlockX() >> 4;
        int centerChunkZ = player.getLocation().getBlockZ() >> 4;

        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        for (int cx = centerChunkX - radiusChunks; cx <= centerChunkX + radiusChunks; cx++) {
            for (int cz = centerChunkZ - radiusChunks; cz <= centerChunkZ + radiusChunks; cz++) {

                if (!world.isChunkLoaded(cx, cz)) {
                    world.getChunkAt(cx, cz);
                }

                var chunk = world.getChunkAt(cx, cz);

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            chunk.setBiome(x, y, z, chosen);
                        }
                    }
                }

                world.refreshChunk(cx, cz);
            }
        }

        player.playSound(player.getLocation(),
                Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                1.0f, 1.0f);

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
            result.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }
        return result.toString().trim();
    }
}