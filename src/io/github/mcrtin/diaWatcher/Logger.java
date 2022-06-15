package io.github.mcrtin.diaWatcher;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import io.github.mcrtin.logToPlayers.LogToPlayers;

public class Logger {

	public void destroy(Location loc, OwnedItemStack owned, String damageCause) {
		if (!owned.hasOwner())
			return;
		owned.transfer(Optional.empty());
		LogToPlayers.info("Destroyed {} at {} from {}, because of §4{}§r.", owned, toString(loc),
				owned.getOwnerString(), damageCause);
	}

	public void playerBreakBlock(Material type, Location loc, OwnedItemStack owned, Player player) {
		if (type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_ORE) {
			final OwnedBlock ownedBlock = new OwnedBlock(loc);
			ownedBlock.getOwner().ifPresent(p -> owned.setOwner(p));
			ownedBlock.remove();
		}
		if (owned.isOwner(player))
			return;
		owned.transfer(Optional.of(player));
		LogToPlayers.info("The §3{}§r at {} droped {} by §b{}§r.", type, toString(loc), owned, player.getName());
	}

	public void playerPickUpItem(Location loc, OwnedItemStack owned, Player player) {
		if (owned.isOwner(player))
			return;
		owned.transfer(Optional.of(player));
		LogToPlayers.info("§b{}§r picked up {} at {} of {}.", player.getName(), owned, toString(loc),
				owned.getOwnerString());
	}

	public void container2player(Player player, Location loc, OwnedItemStack owned) {
		if (owned.isOwner(player))
			return;
		owned.transfer(Optional.of(player));
		LogToPlayers.info("Transfered {} from {} to §b{}§r at {}.", owned, owned.getOwnerString(), player.getName(),
				toString(loc));
	}

	private String toString(Location location) {
		return "§6" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + "§r";
	}

}
