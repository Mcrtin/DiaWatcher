package io.github.mcrtin.diaWatcher;

import java.util.Map.Entry;
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

public class DiaListener implements Listener {
	private final Logger log = new Logger();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onItemDespawn(ItemDespawnEvent e) {
		final Item item = e.getEntity();
		if (hasDias(item.getItemStack()))
			log.destroy(item, Optional.empty());

	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onItemDeath(EntityDamageEvent e) {//TODO
		if (e.getEntityType() != EntityType.DROPPED_ITEM)
			return;
		final Item item = (Item) e.getEntity();
		if (hasDias(item.getItemStack()))
			log.destroy(item, Optional.of(e.getCause()));
	}

//	@EventHandler
//	public void onItemDrop(PlayerDropItemEvent e) {
//		final Item item = e.getItemDrop();
//		if (hasDias(item.getItemStack()))
//			log.playerDropItem(e.getPlayer(), item);
//
//	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockBreak(BlockDropItemEvent e) {
		for (Item item : e.getItems())
			if (hasDias(item.getItemStack()))
				log.playerBreakBlock(e.getBlockState(), e.getPlayer(), item);

	}

//	@EventHandler
//	public void onItemDrop(EntityDropItemEvent e) {
//		final Item item = e.getItemDrop();// TODO onEntityDeath?
//		if (hasDias(item.getItemStack()))
//			log.entity2item(e.getEntity(), item, "DROP");
//	}

//	@EventHandler
//	public void onItemSpawn(ItemSpawnEvent e) {
//	}

//	@EventHandler
//	public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
//		if (e.getRightClicked().getType() != EntityType.ITEM_FRAME)
//			return;
//		final ItemStack itemStack = e.getPlayer().getInventory().getItem(e.getHand());
//		if (hasDias(itemStack))
//			log.player2entity(e.getPlayer(), e.getRightClicked(), itemStack);
//	}
//
//	@EventHandler
//	public void onPlayerInventory(InventoryMoveItemEvent e) {
//		final ItemStack itemStack = e.getItem();
//		if (hasDias(itemStack))
//			log.container2container(itemStack, e.getSource(), e.getDestination());
//	}
//
//	@EventHandler
//	public void onPlayerInventory2(InventoryPickupItemEvent e) {
//		final Item item = e.getItem();
//		if (hasDias(item.getItemStack()))
//			log.item2container(item, e.getInventory());
//	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
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
		case COLLECT_TO_CURSOR:
			ItemStack itemStack = e.getCursor();
			int i = itemStack.getAmount();
			int max = itemStack.getMaxStackSize();
			if (i == max)
				return;
			if (!hasDias(itemStack))
				return;
			if (clickBottom)
				for (Entry<Integer, ? extends ItemStack> entry : topInventory.all(itemStack).entrySet())
					log.container2player(player, topInventory, itemStack);
//			else
//				for (Entry<Integer, ? extends ItemStack> entry : bottomInventory.all(itemStack).entrySet())
//					log.player2container(player, topInventory, itemStack);
			break;
		case HOTBAR_MOVE_AND_READD:
			if (clickBottom)
				return;
			itemStack = e.getCurrentItem();
			if (hasDias(itemStack))
				log.container2player(player, topInventory, itemStack);
			break;
		case MOVE_TO_OTHER_INVENTORY:
			itemStack = e.getCurrentItem();
			if (hasDias(itemStack))
				if (!clickBottom)
					log.container2player(player, topInventory, itemStack);
//				else
//					log.player2container(player, topInventory, itemStack);

			break;
		case PICKUP_ALL:
			itemStack = e.getCurrentItem();
			if (!clickBottom && hasDias(itemStack))
				log.container2player(player, topInventory, itemStack);
			break;
		case PICKUP_HALF:
			itemStack = e.getCurrentItem();
			if (!clickBottom && hasDias(itemStack)) {
				itemStack = itemStack.clone();
				itemStack.setAmount(itemStack.getAmount() / 2);// TODO bug? (rounds down)
				log.container2player(player, topInventory, itemStack);
			}
			break;
		case PICKUP_ONE:
			itemStack = e.getCurrentItem();
			if (!clickBottom && hasDias(itemStack)) {
				itemStack = e.getCurrentItem().clone();
				itemStack.setAmount(1);
				log.container2player(player, topInventory, itemStack);
			}
			break;
		case PICKUP_SOME:
			itemStack = e.getCurrentItem();
			if (!clickBottom && hasDias(itemStack)) {
				itemStack = e.getCurrentItem().clone();
				itemStack.setAmount(e.getCursor().getMaxStackSize() - e.getCursor().getAmount());
				log.container2player(player, topInventory, itemStack);
			}
			break;
//		case PLACE_ALL:
//			itemStack = e.getCursor();
//			if (!clickBottom && hasDias(itemStack))
//				log.player2container(player, topInventory, itemStack);
//			break;
//		case PLACE_ONE:
//			itemStack = e.getCurrentItem();
//			if (!clickBottom && hasDias(itemStack)) {
//				itemStack = e.getCursor().clone();
//				itemStack.setAmount(1);
//				log.player2container(player, topInventory, itemStack);
//			}
//			break;
//		case PLACE_SOME:
//			itemStack = e.getCurrentItem();
//			if (!clickBottom && hasDias(itemStack)) {
//				itemStack = e.getCursor().clone();
//				itemStack.setAmount(e.getCurrentItem().getMaxStackSize() - e.getCurrentItem().getAmount());
//				log.player2container(player, topInventory, itemStack);
//			}
//			break;
		case SWAP_WITH_CURSOR:
			if (!clickBottom) {
//				itemStack = e.getCursor();
//				if (hasDias(itemStack))
//					log.player2container(player, topInventory, itemStack);
				itemStack = e.getCurrentItem();
				if (hasDias(itemStack))
					log.container2player(player, topInventory, itemStack);
			}
			break;
		default:
			break;

		}
	}

//	@EventHandler
//	public void onDispense(BlockDispenseEvent e) {
//		if (hasDias(e.getItem()))
//			log.container2item(e.getBlock(), e.getItem());
//	}

//	@EventHandler
//	public void onDrop(BlockDropItemEvent e) {
//	}
//	@EventHandler
//	public void entityDeath(EntityDeathEvent e) {
//		final LivingEntity entity = e.getEntity();
//		final EntityEquipment equipment = entity.getEquipment();
//		for (ItemStack itemStack : equipment.getArmorContents()) {
//			if (hasDias(itemStack))
//				log.entity2item(entity, itemStack, "DEATH");
//		}
//		ItemStack itemStack = equipment.getItemInMainHand();
//		if (hasDias(itemStack))
//			log.entity2item(entity, itemStack, "DEATH");
//		itemStack = equipment.getItemInOffHand();
//		if (hasDias(itemStack))
//			log.entity2item(entity, itemStack, "DEATH");
//	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onEntityPickupItem(EntityPickupItemEvent e) {
		final Item item = e.getItem();
		final LivingEntity entity = e.getEntity();
		if (entity.getType() != EntityType.PLAYER)
			return;
		if (hasDias(item.getItemStack()))
			log.playerPickUpItem(item, (Player) entity, e.getRemaining());
	}

//	@EventHandler
//	public void onEntityDropItem(EntityDropItemEvent e) {
//		
//	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if (!e.canBuild())
			return;
		final ItemStack itemInHand = e.getItemInHand();
		if (hasDias(itemInHand))
			log.playerPlaceBlock(e.getBlock(), itemInHand, e.getPlayer());
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

//	private boolean hasDias(Block block) {
//		if (!(block.getState() instanceof Container))
//			return block.getType() == Material.DIAMOND_ORE || block.getType() == Material.DIAMOND_BLOCK;
//		Container container = (Container) block.getState();
//		final Inventory snapshotInventory = container.getSnapshotInventory();
//		if (snapshotInventory.contains(Material.DIAMOND))
//			return true;
//		if (snapshotInventory.contains(Material.DIAMOND_ORE))
//			return true;
//		if (snapshotInventory.contains(Material.DIAMOND_BLOCK))
//			return true;
//		HashMap<Integer, ? extends ItemStack> shulkers;
//		if ((shulkers = snapshotInventory.all(Material.SHULKER_BOX)).isEmpty())
//			for (ItemStack shuker : shulkers.values())
//				if (hasDias(shuker))
//					return true;
//		return false;
//	}
}
