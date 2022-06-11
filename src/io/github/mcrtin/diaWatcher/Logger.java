package io.github.mcrtin.diaWatcher;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.mcrtin.logToPlayers.LogToPlayers;

public class Logger {

	public void destroy(Item item, Optional<DamageCause> damageCause) {
		final ItemStack itemStack = item.getItemStack();
		final OwnedItemStack owned = new OwnedItemStack(itemStack);
		owned.transfer(Optional.empty());
		LogToPlayers.info("Destroyed {} at {} from {}, because of §4{}§r.", toString(itemStack),
				toString(item.getLocation()), owned, damageCause.isPresent() ? damageCause.get() : "DESPAWN");
	}

	public void playerBreakBlock(BlockState blockState, Player player, Item item) {
		final Material type = blockState.getType();
		if (type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_ORE) {
			final OwnedBlock ownedBlock = new OwnedBlock(blockState);
			if (ownedBlock.isOwner(player))
				return;
			final Optional<OfflinePlayer> blockOwner = ownedBlock.getOwner();
			final ItemStack itemStack = item.getItemStack();
			final OwnedItemStack owned = new OwnedItemStack(itemStack);
			blockOwner.ifPresent(p -> owned.setOwner(player));
			owned.transfer(Optional.of(player));
			ownedBlock.remove();
			LogToPlayers.info("The §3{}§r at {} droped {} by §b{}§r.", blockState.getType(),
					toString(blockState.getLocation()), toString(itemStack), player.getName());
			return;
		} // Shulker
		final ItemStack itemStack = item.getItemStack();
		final OwnedItemStack owned = new OwnedItemStack(itemStack);
		owned.transfer(Optional.of(player));
		LogToPlayers.info("The §3{}§r at {} droped {} by §b{}§r.", blockState.getType(),
				toString(blockState.getLocation()), toString(itemStack), player.getName());
	}

	public void playerPickUpItem(Item item, Player player, int remaining) {
		final ItemStack itemStack = remaining != 0 ? item.getItemStack().clone() : item.getItemStack();
		if (remaining != 0)
			itemStack.setAmount(itemStack.getAmount() - remaining);
		final OwnedItemStack owned = new OwnedItemStack(itemStack);

		owned.transfer(Optional.of(player));
		LogToPlayers.info("§b{}§r picked up {} at {} of {}.", player.getName(), toString(itemStack),
				toString(item.getLocation()), owned);
	}

	public void container2player(Player player, Inventory from, ItemStack itemStack) {
		final OwnedItemStack owned = new OwnedItemStack(itemStack);
		owned.transfer(Optional.of(player));
		LogToPlayers.info("Transfered {} from {} to §b{}§r at {}.", toString(itemStack), owned, player.getName(),
				toString(from.getLocation()));
	}

	private String toString(Location location) {
		return "§6" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + "§r";
	}

	private String toString(ItemStack itemStack) {
		StringBuilder toString = new StringBuilder("§9").append(itemStack.getType().name()).append(" §8x §a")
				.append(itemStack.getAmount()).append("§r");
		return toString.toString();
	}

}
