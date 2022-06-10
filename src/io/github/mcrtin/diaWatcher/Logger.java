package io.github.mcrtin.diaWatcher;

import static io.github.mcrtin.diaWatcher.Econemie.ECO;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.mcrtin.logToPlayers.LogToPlayers;

public class Logger {

	public void destroy(Item item, Optional<DamageCause> damageCause) {
		final ItemStack itemStack = item.getItemStack();
		final Optional<OfflinePlayer> owner = getOwner(itemStack);
		final Optional<String> ownerName = getOwnerName(owner);
		owner.ifPresent(player -> ECO.subtract(new DiaCount(itemStack), player));

		LogToPlayers.info("Destroyed {} at {} from §b{}§r, because of §4{}§r.", toString(itemStack),
				toString(item.getLocation()), ownerName.orElse("§7§omissing"),
				damageCause.isPresent() ? damageCause.get() : "DESPAWN");
	}

//	@Override
//	public void playerDropItem(Player player, Item item) {
//		final ItemStack itemStack = item.getItemStack();
//		final Optional<OfflinePlayer> owner = getOwner(itemStack);
//		assert owner.isPresent();
//		assert owner.get().getUniqueId() == player.getUniqueId();
//		LogToPlayers.info("Droped {} at {}. Owner: {}", toString(itemStack), toString(item.getLocation()),
//				player.getName());
//
//	}
//
//	// item frame, minecarts
//	public void player2entity(Player player, Entity entity, ItemStack itemStack) {
//		LogToPlayers.info("player {} ({}) gave entity {} (at {}) {}", player.getUniqueId(), player.getName(),
//				entity.getUniqueId(), entity.getLocation(), itemStack);
//	}
//
//	// chest, hopper, ec, shulker, dropper, dispenser
//	public void player2container(HumanEntity player, Inventory to, ItemStack itemStack) {
//		LogToPlayers.info("player {} transfered {} to {}", player, itemStack, to.getLocation());
//	}
	public void container2player(Player player, Inventory from, ItemStack itemStack) {
		final Optional<OfflinePlayer> owner = getOwner(itemStack);
		final Optional<String> ownerName = getOwnerName(owner);
		if (owner.isPresent() && owner.get().getUniqueId().equals(player.getUniqueId()))
			return;
		owner.ifPresentOrElse(p -> ECO.transfer(new DiaCount(itemStack), p, player),
				() -> ECO.add(new DiaCount(itemStack), player));
		LogToPlayers.info("Transfered {} from §b{}§r to §b{}§r at {}.", ownerName.orElse("§7§omissing§r"),
				toString(itemStack), player.getName(), toString(from.getLocation()));
	}
//
//	// hopper, dispenser, dropper
//	public void container2container(ItemStack itemStack, Inventory from, Inventory to) {
//		LogToPlayers.info("moved {} from {} to {}", itemStack, from.getLocation(), to.getLocation());
//	}
//
//	// hopper
//	public void item2container(Item item, Inventory inventory) {
//		LogToPlayers.info("Picked up {} at {} by {}", item.getItemStack(), inventory.getLocation(),
//				inventory.getHolder().getClass());
//	}
//
//	// dropper, dispenser
//	public void container2item(Block block, ItemStack itemStack) {
//		LogToPlayers.info("dropped {} by {}", itemStack, block);
//	}
//
//	// itemframe, death
//	public void entity2item(Entity entity, Item item, String cause) {
//		LogToPlayers.info("entity {} droped {} because of {}", entity, item.getItemStack(), cause);
//	}

// TODO old owner
	public void playerBreakBlock(BlockState blockState, Player player, Item item) {
		final ItemStack itemStack = item.getItemStack();
		setOwner(itemStack, player);
		ECO.add(new DiaCount(itemStack), player);
		LogToPlayers.info("The §3{}§r at {} droped {} by §b{}§r.", blockState.getType(),
				toString(blockState.getLocation()), toString(itemStack), player.getName());
	}

	// TODO old owner
	public void playerPlaceBlock(Block block, ItemStack itemStack, Player player) {
		ItemStack clone = itemStack.clone();
		clone.setAmount(1);
		ECO.subtract(new DiaCount(clone), player);
		LogToPlayers.info("Placed {} at {} by §b{}§r.", toString(clone), toString(block.getLocation()),
				player.getName());

		final Optional<OfflinePlayer> owner = getOwner(itemStack);
		assert owner.isPresent() && owner.get().getUniqueId().equals(player.getUniqueId());
	}

	public void playerPickUpItem(Item item, Player player, int remaining) {
		final ItemStack itemStack = item.getItemStack().clone();
		itemStack.setAmount(itemStack.getAmount() - remaining);
		final Optional<OfflinePlayer> owner = getOwner(itemStack);
		final Optional<String> ownerName = getOwnerName(owner);

		if (owner.isPresent() && owner.get().getUniqueId().equals(player.getUniqueId()))
			return;

		owner.ifPresentOrElse(p -> ECO.transfer(new DiaCount(itemStack), p, player),
				() -> ECO.add(new DiaCount(itemStack), player));
		LogToPlayers.info("§b{}§r picked up {} at {} of §a{} ", player.getName(), toString(itemStack),
				toString(item.getLocation()), ownerName.orElse("§7§omissing"));
	}

	private static final NamespacedKey OwnerKey = new NamespacedKey(Main.getPlugin(), "Owner");

	private Optional<OfflinePlayer> getOwner(ItemStack itemStack) {
		if (!itemStack.hasItemMeta())
			return Optional.empty();

		ItemMeta itemMeta = itemStack.getItemMeta();
		final PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
		if (!pdc.has(OwnerKey, PersistentDataType.STRING))
			return Optional.empty();
		final String owner = pdc.get(OwnerKey, PersistentDataType.STRING);
		try {
			return Optional.of(Bukkit.getOfflinePlayer(UUID.fromString(owner)));
		} catch (IllegalArgumentException ex) {
			return Optional.empty();
		}
	}

	private void setOwner(ItemStack itemStack, Player player) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.getPersistentDataContainer().set(OwnerKey, PersistentDataType.STRING, player.getUniqueId().toString());
		itemStack.setItemMeta(itemMeta);
	}

	private Optional<String> getOwnerName(Optional<OfflinePlayer> owner) {
		return owner.map(player -> player.getName());
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
