package io.github.mcrtin.diaWatcher;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface ILogger {
	void destroy(Item item, Optional<DamageCause> damageCause);

	void container2player(Player player, Inventory from, ItemStack itemStack);

	void playerBreakBlock(Block block, Player player, Item item);

	void playerPlaceBlock(Block block, ItemStack itemStack, Player player);

	void playerPickUpItem(Item item, Player player, int remaining);
}
