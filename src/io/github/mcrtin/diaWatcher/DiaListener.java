package io.github.mcrtin.diaWatcher;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent;

public class DiaListener implements Listener {
	private final Logger log = new Logger();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onItemDespawn(ItemDespawnEvent e) {
		final Item entity = e.getEntity();
		final ItemStack itemStack = entity.getItemStack();
		if (!hasDias(itemStack))
			return;
		log.destroy(entity.getLocation(), new OwnedItemStack(itemStack), "DESPAWN");

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onItemDeath(EntityDamageEvent e) {
		if (e.getEntityType() != EntityType.DROPPED_ITEM)
			return;
		final double damage = e.getDamage();
		if (damage <= 0)
			return;
		final Item item = (Item) e.getEntity();
		if (NmsItem.getHealth(item) > damage)
			return;

		final ItemStack itemStack = item.getItemStack();
		if (!hasDias(itemStack))
			return;
		log.destroy(item.getLocation(), new OwnedItemStack(itemStack), e.getCause().name());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onBlockBreak(BlockDropItemEvent e) {
		final Material type = e.getBlockState().getType();
		final Location location = e.getBlock().getLocation();
		e.getItems().stream().filter(item -> hasDias(item.getItemStack())).forEach(
				item -> log.playerBreakBlock(type, location, new OwnedItemStack(item.getItemStack()), e.getPlayer()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent e) {
		final HumanEntity hEntity = e.getWhoClicked();
		if (hEntity.getType() != EntityType.PLAYER)
			return;
		final Player player = Bukkit.getPlayer(hEntity.getUniqueId());
		final Inventory topInventory = e.getInventory();
		final Inventory bottomInventory = e.getView().getBottomInventory();
		final Location loc = topInventory.getLocation();
		final boolean clickBottom = e.getClickedInventory().equals(bottomInventory);
		ItemStack itemStack;
		switch (e.getAction()) {
		case CLONE_STACK:// TODO
			return;
		case HOTBAR_MOVE_AND_READD:// swap? (hotkeying)
			if (clickBottom)
				return;
			itemStack = e.getCursor();
			break;
		case MOVE_TO_OTHER_INVENTORY:
			if (clickBottom)
				return;
			itemStack = e.getCurrentItem();
			break;
		case PICKUP_ALL:
		case PICKUP_HALF:
		case PICKUP_ONE:
			if (clickBottom)
				return;
			itemStack = e.getCurrentItem();
			break;
		case SWAP_WITH_CURSOR:// TODO - stack blocks
			if (clickBottom)
				return;
			itemStack = e.getCurrentItem();
			break;
		default:
			return;
		}
		if (hasDias(itemStack))
			log.container2player(player, loc, new OwnedItemStack(itemStack));
	}

	@EventHandler
	public void onBlockBreak(CustomBlockDataRemoveEvent e) {
		final Material type = e.getBlock().getType();
		if (type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_ORE)
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onEntityPickupItem(EntityPickupItemEvent e) {
		final Item item = e.getItem();
		if (e.getEntityType() != EntityType.PLAYER)
			return;
		final Player player = (Player) e.getEntity();
		final ItemStack itemStack = item.getItemStack();
		if (!hasDias(itemStack))
			return;

		log.playerPickUpItem(item.getLocation(), new OwnedItemStack(itemStack), player);// Remaining ignored cuz if
																						// there are remaining, they are
																						// already his
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if (!e.canBuild())
			return;
		final Material type = e.getItemInHand().getType();
		final Player player = e.getPlayer();
		if (type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_ORE)
			if (new OwnedItemStack(e.getItemInHand()).isOwner(player))
				new OwnedBlock(e.getBlock()).setOwner(player);
	}

	private boolean hasDias(ItemStack itemStack) {
		if (itemStack == null)
			return false;
		switch (itemStack.getType()) {
		case DIAMOND, DIAMOND_ORE, DIAMOND_BLOCK:
			return true;
		case SHULKER_BOX:
			final ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta instanceof BlockStateMeta) {
				BlockStateMeta im = (BlockStateMeta) itemMeta;
				if (im.getBlockState() instanceof Container) {
					Container container = (Container) im.getBlockState();
					final Inventory snapshotInventory = container.getSnapshotInventory();
					if (snapshotInventory.contains(Material.DIAMOND))
						return true;
					if (snapshotInventory.contains(Material.DIAMOND_ORE))
						return true;
					if (snapshotInventory.contains(Material.DIAMOND_BLOCK))
						return true;
				}
			}
		default:
			return false;
		}
	}
}
