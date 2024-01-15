package com.thefishnextdoor.dontpickup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerTracker {

    private static ArrayList<TrackedPlayer> trackedPlayers = new ArrayList<>();

    public static class TrackedPlayer {

        private final UUID id;
        private ArrayList<Material> dontPickUp = new ArrayList<>();
        private boolean changes = false;

        public TrackedPlayer(Player player) {
            this.id = player.getUniqueId();
            trackedPlayers.add(this);
            load();
        }

        public boolean is(Player player) {
            return player.getUniqueId().equals(id);
        }

        public void dontPickUp(Material material) {
            if (!dontPickUp.contains(material)) {
                dontPickUp.add(material);
                changes = true;
            }
        }

        public void pickUp(Material material) {
            if (dontPickUp.remove(material)) {
                changes = true;
            }

        }

        public ArrayList<Material> notPickingUp() {
            return dontPickUp;
        }

        public void saveAndClose() {
            save();
            trackedPlayers.remove(this);
        }

        public void save() {
            if (!changes) {
                return;
            }

            File playerFile = getPlayerFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            
            ArrayList<String> materialNames = new ArrayList<>();
            for (Material material : dontPickUp) {
                materialNames.add(material.name());
            }

            config.set("DontPickUpMaterials", materialNames);

            try {
                config.save(playerFile);
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                changes = false;
            }
        }

        private void load() {
            File playerFile = getPlayerFile();
            if (!playerFile.exists()) {
                return;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            dontPickUp.clear();

            List<String> materialNames = config.getStringList("DontPickUpMaterials");
            for (String name : materialNames) {
                Material material = Material.matchMaterial(name);
                if (material != null) {
                    dontPickUp.add(material);
                }
            }
        }

        private File getPlayerFile() {
            return new File(FileSystem.getDataFolder(), id + ".yml");
        }
    }

    public static TrackedPlayer get(Player player) {
        for (TrackedPlayer trackedPlayer : trackedPlayers) {
            if (trackedPlayer.is(player)) {
                return trackedPlayer;
            }
        }
        return new TrackedPlayer(player);
    }

    public static void remove(Player player) {
        for (TrackedPlayer trackedPlayer : trackedPlayers) {
            if (trackedPlayer.is(player)) {
                trackedPlayer.saveAndClose();
                return;
            }
        }
    }

    public static void saveAll() {
        for (TrackedPlayer trackedPlayer : trackedPlayers) {
            trackedPlayer.save();
        }
    }
}
