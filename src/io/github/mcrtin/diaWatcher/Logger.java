package io.github.mcrtin.diaWatcher;

import io.github.mcrtin.logToPlayers.LogToPlayers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Logger {

	public void destroy(Location loc, OwnedItemStack owned, String damageCause) {
		if (!owned.hasOwner())
			return;
		owned.transfer(null);
		LogToPlayers.info("Destroyed {} at {} from {}, because of \u00474{}\u0047r.", owned, toString(loc),
				owned.getOwnerString(), damageCause);
	}

	public void playerBreakBlock(Material type, Location loc, OwnedItemStack owned, Player player) {
		if (type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_ORE) {
			OfflinePlayer owner = new OwnedBlock(loc).getOwner();
			if (owner != null)
				owned.setOwner(owner);
		}
		if (type == owned.getItemStack().getType())
			if (owned.isOwner(player))
				return;
		owned.transfer(player);
		LogToPlayers.info("The \u00473{}\u0047r at {} dropped {} by \u0047b{}\u0047r.", type, toString(loc), owned, toString(player));
	}

	public void playerPickUpItem(Location loc, OwnedItemStack owned, Player player) {
		if (owned.isOwner(player))
			return;
		owned.transfer(player);
		LogToPlayers.info("\u0047b{}\u0047r picked up {} at {} of {}.", toString(player), owned, toString(loc),
				owned.getOwnerString());
	}

	public void container2player(Player player, Location loc, OwnedItemStack owned) {
		if (owned.isOwner(player))
			return;
		owned.transfer(player);
		LogToPlayers.info("Transferred {} from {} to {} at {}.", owned, owned.getOwnerString(), toString(player),
				toString(loc));
	}

	public void smelt(Location loc, OwnedItemStack source, OwnedItemStack result) {
		result.transfer(source.getOwner());
		LogToPlayers.info("Smelted {} to {} of {} at {}.", source, result, source.getOwnerString(), toString(loc));
	}
	private String toString(Location location) {
		return "\u00476" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + "\u0047r";
	}

	private String toString(Player player) {
		return "\u0047b" + player.getName() + "\u0047r";
	}


}
