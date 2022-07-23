package io.github.mcrtin.diaWatcher;

import com.jeff_media.customblockdata.CustomBlockData;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

@Data
public class OwnedBlock implements Owned {
	private final CustomBlockData pdc;
	@Nullable
	private OfflinePlayer owner = null;

	public OwnedBlock(CustomBlockData pdc) {
		this.pdc = pdc;
		if (!pdc.has(ownerKey, PersistentDataType.STRING))
			return;
		final String owner = pdc.get(ownerKey, PersistentDataType.STRING);
		assert owner != null;
		this.owner = Bukkit.getOfflinePlayer(UUID.fromString(owner));
	}

	@Override
	public void setOwner(@NotNull OfflinePlayer player) {
		owner = player;
		pdc.set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());
	}

	@Override
	public boolean isOwner(OfflinePlayer player) {
		return owner != null && owner.equals(player);
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
