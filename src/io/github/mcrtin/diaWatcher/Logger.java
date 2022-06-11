package io.github.mcrtin.diaWatcher;

import static io.github.mcrtin.diaWatcher.Econemie.ECO;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.mcrtin.logToPlayers.LogToPlayers;

public class Logger {

	public void destroy(Item item, Optional<DamageCause> damageCause) {
		final ItemStack itemStack = item.getItemStack();
		final DiaCount diaCount = new DiaCount(itemStack);
		final OwnedItemStack owner = new OwnedItemStack(itemStack);
		if (diaCount.isEmpty())
			return;
		owner.getOwner().ifPresent(player -> ECO.subtract(diaCount, player));

		LogToPlayers.info("Destroyed {} at {} from {}, because of §4{}§r.", toString(itemStack),
				toString(item.getLocation()), owner, damageCause.isPresent() ? damageCause.get() : "DESPAWN");
	}

	// TODO old owner
	public void playerBreakBlock(BlockState blockState, Player player, Item item) {
		final Material type = blockState.getType();
		if (type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_ORE) {
			final OwnedBlock ownedBlock = new OwnedBlock(blockState);
			if (ownedBlock.isOwner(player))
				return;
			final Optional<OfflinePlayer> blockOwner = ownedBlock.getOwner();
			final ItemStack itemStack = item.getItemStack();
			final OwnedItemStack owner = new OwnedItemStack(itemStack);
			blockOwner.ifPresentOrElse(p -> ECO.transfer(itemStack, p, player), () -> ECO.add(itemStack, player));
			owner.setOwner(player);
			ownedBlock.remove();
			LogToPlayers.info("The §3{}§r at {} droped {} by §b{}§r.", blockState.getType(),
					toString(blockState.getLocation()), toString(itemStack), player.getName());
			return;
		} // Shulker
		final ItemStack itemStack = item.getItemStack();
		final DiaCount diaCount = new DiaCount(itemStack);
		if (diaCount.isEmpty())
			return;

		final ItemMeta itemMeta = itemStack.getItemMeta();
		if (!(itemMeta instanceof BlockStateMeta))
			return;
		BlockStateMeta im = (BlockStateMeta) itemMeta;
		if ((im.getBlockState() instanceof Container))
			return;
		Container container = (Container) im.getBlockState();
		final Inventory snapshotInventory = container.getSnapshotInventory();

		snapshotInventory.forEach(i -> {
			final OwnedItemStack owner2 = new OwnedItemStack(i);
			final DiaCount diaCount2 = new DiaCount(i);
			if (diaCount2.isEmpty())
				return;
			owner2.getOwner().ifPresentOrElse(p -> ECO.transfer(itemStack, p, player),
					() -> ECO.add(itemStack, player));
		});

		LogToPlayers.info("The §3{}§r at {} droped {} by §b{}§r.", blockState.getType(),
				toString(blockState.getLocation()), toString(itemStack), player.getName());
	}

	// old owner
//	public void playerPlaceBlock(Block block, ItemStack itemStack, Player player) {
//		new OwnedBlock(block).setOwner(player);
//		if (itemStack.getAmount() != 1) {
//			ItemStack clone = itemStack.clone();
//			clone.setAmount(1);
//			ECO.subtract(clone, player);
//		} else
//			ECO.subtract(itemStack, player);
//		LogToPlayers.info("Placed {} at {} by §b{}§r.", itemStack.getType(), toString(block.getLocation()),
//				player.getName());
//
//		final OwnedItemStack owner = new OwnedItemStack(itemStack);
//		assert owner.isOwner(player);
//	}

	public void playerPickUpItem(Item item, Player player, int remaining) {
		final ItemStack itemStack = remaining != 0 ? item.getItemStack().clone() : item.getItemStack();
		if (remaining != 0)
			itemStack.setAmount(itemStack.getAmount() - remaining);
		final OwnedItemStack owner = new OwnedItemStack(itemStack);

		if (owner.isOwner(player))
			return;

		owner.getOwner().ifPresentOrElse(p -> ECO.transfer(itemStack, p, player), () -> ECO.add(itemStack, player));
		LogToPlayers.info("§b{}§r picked up {} at {} of {}.", player.getName(), toString(itemStack),
				toString(item.getLocation()), owner);
	}

	public void container2player(Player player, Inventory from, ItemStack itemStack) {
		final OwnedItemStack owner = new OwnedItemStack(itemStack);
		if (owner.isOwner(player))
			return;
		owner.getOwner().ifPresentOrElse(p -> ECO.transfer(itemStack, p, player), () -> ECO.add(itemStack, player));
		LogToPlayers.info("Transfered {} from {} to §b{}§r at {}.", toString(itemStack), owner, player.getName(),
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
