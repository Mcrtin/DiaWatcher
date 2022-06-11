package io.github.mcrtin.diaWatcher;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import lombok.Data;

@Data
public class OwnedItemStack implements Owned {
	private final ItemStack itemStack;
	@Nullable
	private Optional<OfflinePlayer> owner = null;
	
	@Override
	public Optional<OfflinePlayer> getOwner() {
		if (owner != null)
			return owner;

		if (!itemStack.hasItemMeta()) {
			owner = Optional.empty();
			return owner;
		}

		ItemMeta itemMeta = itemStack.getItemMeta();
		final PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
		if (!pdc.has(OwnerKey, PersistentDataType.STRING)) {
			owner = Optional.empty();
			return owner;
		}

		try {
			final String owner = pdc.get(OwnerKey, PersistentDataType.STRING);
			this.owner = Optional.of(Bukkit.getOfflinePlayer(UUID.fromString(owner)));
		} catch (IllegalArgumentException ex) {
			owner = Optional.empty();

		}
		return owner;
	}

	@Override
	public void setOwner(Player player) {
		owner = Optional.of(player);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.getPersistentDataContainer().set(OwnerKey, PersistentDataType.STRING, player.getUniqueId().toString());

		if (itemStack.getType() != Material.SHULKER_BOX) {
			itemStack.setItemMeta(itemMeta);
			return;
		}
		if (!(itemMeta instanceof BlockStateMeta))
			return;
		BlockStateMeta bm = (BlockStateMeta) itemMeta;
		if ((bm.getBlockState() instanceof Container))
			return;
		Container container = (Container) bm.getBlockState();
		final Inventory snapshotInventory = container.getSnapshotInventory();
		snapshotInventory.forEach(i -> new OwnedItemStack(itemStack).setOwner(player));
		itemStack.setItemMeta(itemMeta);
	}

	@Override
	public boolean isOwner(Player player) {
		return getOwner().filter(p -> p.equals(player)).isPresent();
	}

	public String toString() {
		return "§b" + getOwnerName().orElse("§7§omissing") + "§r";
	}
}
