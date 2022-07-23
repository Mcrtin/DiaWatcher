package io.github.mcrtin.diaWatcher;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static io.github.mcrtin.diaWatcher.Economie.ECO;

@Data
public class OwnedItemStack implements Owned {
	private final ItemStack itemStack;

	@Nullable
	private OfflinePlayer owner = null;

	public OwnedItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		if (!itemStack.hasItemMeta())
			return;

		ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		final PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
		if (!pdc.has(ownerKey, PersistentDataType.STRING))
			return;
		final String owner = pdc.get(ownerKey, PersistentDataType.STRING);
		assert owner != null;
		this.owner = Bukkit.getOfflinePlayer(UUID.fromString(owner));
	}

	public void transfer(@Nullable OfflinePlayer to) {
		if (Objects.equals(owner, to))
			return;
		forEach(i -> i.transfer(to));
		final DiaCount diaCount = new DiaCount(itemStack.getType(), itemStack.getAmount());
		if (diaCount.isEmpty())
			return;

		if (to != null) {
			if (owner == null)
				ECO.add(diaCount, to);
			else if (!owner.equals(to))
					ECO.transfer(diaCount, owner, to);
			setOwner(to);
			return;
		}

		if (owner == null)
			return;

		ECO.subtract(diaCount, owner);
		removeOwner();
	}

	private void forEach(Consumer<OwnedItemStack> action) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (!(itemMeta instanceof BlockStateMeta bm))
			return;
		if (!(bm.getBlockState() instanceof Container container))
			return;
		final Inventory snapshotInventory = container.getSnapshotInventory();
		snapshotInventory.forEach(i -> action.accept(new OwnedItemStack(i)));
		itemStack.setItemMeta(itemMeta);
	}

	public void removeOwner() {
		owner = null;
		if (!itemStack.hasItemMeta())
			return;
		ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		itemMeta.getPersistentDataContainer().remove(ownerKey);
		itemStack.setItemMeta(itemMeta);
	}

	@Override
	public void setOwner(@NotNull OfflinePlayer player) {
		owner = player;
		ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		itemMeta.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());
		itemStack.setItemMeta(itemMeta);
	}

	@Override
	public boolean isOwner(@NotNull OfflinePlayer player) {
		return owner != null && owner.equals(player);
	}

	public String getOwnerString() {
		return "\u0047b" + getOwnerName().orElse("\u00477\u0047omissing") + "\u0047r";
	}

	public String toString() {
		return "\u00479" + itemStack.getType().name() + " \u00478x \u0047a" +
				itemStack.getAmount() + "\u0047r";
	}

	public boolean hasOwner() {
		return owner != null;
	}
}
