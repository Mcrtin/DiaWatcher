package io.github.mcrtin.diaWatcher;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;

import com.jeff_media.customblockdata.CustomBlockData;

import lombok.Data;

@Data
public class OwnedBlock implements Owned {
	private final CustomBlockData pdc;
	@Nullable
	private Optional<OfflinePlayer> owner = null;

	@Override
	public Optional<OfflinePlayer> getOwner() {
		if (owner != null)
			return owner;
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
	public void setOwner(OfflinePlayer player) {
		owner = Optional.of(player);
		pdc.set(OwnerKey, PersistentDataType.STRING, player.getUniqueId().toString());
	}

	@Override
	public boolean isOwner(OfflinePlayer player) {
		return getOwner().filter(p -> p.equals(player)).isPresent();
	}

	public OwnedBlock(Location loc) {
		this.pdc = new CustomBlockData(loc.getBlock(), Main.getPlugin());
	}

	public OwnedBlock(Block block) {
		this.pdc = new CustomBlockData(block, Main.getPlugin());
	}

	public void remove() {
		pdc.clear();
	}
}
