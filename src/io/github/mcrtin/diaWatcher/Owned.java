package io.github.mcrtin.diaWatcher;

import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;

public interface Owned {
	static final NamespacedKey OwnerKey = new NamespacedKey(Main.getPlugin(), "Owner");

	Optional<OfflinePlayer> getOwner();

	void setOwner(OfflinePlayer player);

	boolean isOwner(OfflinePlayer player);

	default Optional<String> getOwnerName() {
		return getOwner().map(player -> player.getName());
	}
}
