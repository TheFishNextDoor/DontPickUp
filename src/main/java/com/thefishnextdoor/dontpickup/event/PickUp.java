package com.thefishnextdoor.dontpickup.event;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.thefishnextdoor.dontpickup.TrackedPlayer;

public class PickUp implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPickUp(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        TrackedPlayer trackedPlayer = TrackedPlayer.get((Player) entity);
        Material material = event.getItem().getItemStack().getType();
        if (!trackedPlayer.canPickUp(material)) {
            event.setCancelled(true);
        }
    }    
}
