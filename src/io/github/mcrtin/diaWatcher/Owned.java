package io.github.mcrtin.diaWatcher;

import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Owned {
	static final NamespacedKey OwnerKey = new NamespacedKey(Main.getPlugin(), "Owner");

	Optional<OfflinePlayer> getOwner();

	void setOwner(Player player);

	boolean isOwner(Player player);

	default Optional<String> getOwnerName() {
		return getOwner().map(player -> player.getName());
	}
}
