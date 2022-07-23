package io.github.mcrtin.diaWatcher;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.datatypes.UuidDataType;
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
		if (pdc.has(ownerKey, UuidDataType.OFFLINE_PLAYER))
			owner = pdc.get(ownerKey, UuidDataType.OFFLINE_PLAYER);
	}

	@Override
	public void setOwner(@NotNull OfflinePlayer player) {
		pdc.set(ownerKey, UuidDataType.OFFLINE_PLAYER, owner = player);
	}

	@Override
	public boolean isOwner(OfflinePlayer player) {
		return owner != null && owner.equals(player);
	}

	public OwnedBlock(Location loc) {
		pdc = new CustomBlockData(loc.getBlock(), Main.getPlugin());
	}

	public OwnedBlock(Block block) {
		pdc = new CustomBlockData(block, Main.getPlugin());
	}

	public void remove() {
		pdc.clear();
	}
}
