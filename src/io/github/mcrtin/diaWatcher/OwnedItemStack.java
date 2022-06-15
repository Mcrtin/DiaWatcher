package io.github.mcrtin.diaWatcher;

import static io.github.mcrtin.diaWatcher.Econemie.ECO;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

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

	public void transfer(Optional<Player> player) {
		forEach(i -> i.transfer(player));

		player.ifPresentOrElse(to -> {
			final DiaCount diaCount = new DiaCount(itemStack.getType(), itemStack.getAmount());
//			if (diaCount.isEmpty())
//				return;
			getOwner().ifPresentOrElse(from -> {
				if (!from.equals(to))
					ECO.transfer(diaCount, from, to);
			}, () -> ECO.add(diaCount, to));
			setOwner(to);
		}, () -> {
			final DiaCount diaCount = new DiaCount(itemStack.getType(), itemStack.getAmount());
			if (diaCount.isEmpty())
				return;
			getOwner().ifPresent(from -> {
				ECO.subtract(diaCount, from);
				removeOwner();
			});
		});
	}

	private void forEach(Consumer<OwnedItemStack> action) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (!(itemMeta instanceof BlockStateMeta))
			return;
		BlockStateMeta bm = (BlockStateMeta) itemMeta;
		if ((bm.getBlockState() instanceof Container))
			return;
		Container container = (Container) bm.getBlockState();
		final Inventory snapshotInventory = container.getSnapshotInventory();
		snapshotInventory.forEach(i -> action.accept(new OwnedItemStack(i)));
		itemStack.setItemMeta(itemMeta);
	}

	public void removeOwner() {
		owner = Optional.empty();
		if (!itemStack.hasItemMeta())
			return;
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.getPersistentDataContainer().remove(OwnerKey);
//		List<String> lore = itemMeta.getLore();
//		if (lore != null)
//			lore = lore.stream().filter(s -> !s.contains("owner: ")).collect(Collectors.toList());
//		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
	}

	@Override
	public void setOwner(OfflinePlayer player) {
		owner = Optional.of(player);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.getPersistentDataContainer().set(OwnerKey, PersistentDataType.STRING, player.getUniqueId().toString());
//		List<String> lore = itemMeta.getLore();
//		lore = lore == null ? lore = new ArrayList<>()
//				: lore.stream().filter(s -> !s.contains("owner: ")).collect(Collectors.toList());
//		lore.add("owner: " + player.getName());
//		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
	}

	@Override
	public boolean isOwner(OfflinePlayer player) {
		return getOwner().filter(p -> p.equals(player)).isPresent();
	}

	public String getOwnerString() {
		return "§b" + getOwnerName().orElse("§7§omissing") + "§r";
	}

	public String toString() {
		StringBuilder toString = new StringBuilder("§9").append(itemStack.getType().name()).append(" §8x §a")
				.append(itemStack.getAmount()).append("§r");
		return toString.toString();
	}

	public boolean hasOwner() {
		return getOwner().isPresent();
	}
}
