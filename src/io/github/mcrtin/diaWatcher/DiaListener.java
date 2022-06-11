package io.github.mcrtin.diaWatcher;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
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
		log.destroy(e.getEntity(), Optional.empty());

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onItemDeath(EntityDamageEvent e) {
		if (e.getEntityType() != EntityType.DROPPED_ITEM)
			return;
		if (e.getDamage() <= 0)
			return;
		if (Main.getHealth((Item) e.getEntity()) <= e.getDamage())
			log.destroy((Item) e.getEntity(), Optional.of(e.getCause()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onBlockBreak(BlockDropItemEvent e) {
		e.getItems().forEach(item -> log.playerBreakBlock(e.getBlockState(), e.getPlayer(), item));

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent e) {
		final HumanEntity hEntity = e.getWhoClicked();
		if (hEntity.getType() != EntityType.PLAYER)
			return;
		final Player player = Bukkit.getPlayer(hEntity.getUniqueId());
		final Inventory topInventory = e.getInventory();
		final Inventory bottomInventory = e.getView().getBottomInventory();
		final boolean clickBottom = e.getClickedInventory().equals(bottomInventory);
		switch (e.getAction()) {
		case CLONE_STACK:// TODO
			break;
		case HOTBAR_MOVE_AND_READD:
			if (clickBottom)
				return;
			ItemStack itemStack = e.getCursor();
			if (hasDias(itemStack))
				log.container2player(player, topInventory, itemStack);
			break;
		case MOVE_TO_OTHER_INVENTORY:
			itemStack = e.getCurrentItem();
			if (clickBottom || !hasDias(itemStack))
				return;
			log.container2player(player, topInventory, itemStack);

			break;
		case PICKUP_ALL:
			itemStack = e.getCurrentItem();
			if (clickBottom || !hasDias(itemStack))
				return;
			log.container2player(player, topInventory, itemStack);
			break;
		case PICKUP_HALF:
			itemStack = e.getCurrentItem();
			if (clickBottom || !hasDias(itemStack))
				return;
			itemStack = itemStack.clone();
			itemStack.setAmount(itemStack.getAmount() / 2);// TODO bug? (rounds down)
			log.container2player(player, topInventory, itemStack);
			break;
		case PICKUP_ONE:
			itemStack = e.getCurrentItem();
			if (clickBottom || !hasDias(itemStack))
				return;
			itemStack = e.getCurrentItem().clone();
			itemStack.setAmount(1);
			log.container2player(player, topInventory, itemStack);
			break;
		case PICKUP_SOME:
			itemStack = e.getCurrentItem();
			if (clickBottom || !hasDias(itemStack))
				return;
			itemStack = e.getCurrentItem().clone();
			itemStack.setAmount(e.getCursor().getMaxStackSize() - e.getCursor().getAmount());
			log.container2player(player, topInventory, itemStack);
			break;
		case SWAP_WITH_CURSOR:
			if (clickBottom)
				return;
			itemStack = e.getCurrentItem();
			if (hasDias(itemStack))
				log.container2player(player, topInventory, itemStack);
			break;
		default:
			break;

		}
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
		final LivingEntity entity = e.getEntity();
		if (entity.getType() != EntityType.PLAYER)
			return;
		if (hasDias(item.getItemStack()))
			log.playerPickUpItem(item, (Player) entity, e.getRemaining());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if (!e.canBuild())
			return;
		final Material type = e.getItemInHand().getType();
		if (type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_ORE)
			new OwnedBlock(e.getBlock()).setOwner(e.getPlayer());
//		if (hasDias(itemInHand))
//			log.playerPlaceBlock(e.getBlock(), itemInHand, e.getPlayer());
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
