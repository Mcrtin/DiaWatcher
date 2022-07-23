package io.github.mcrtin.diaWatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Singleton
public class Economie {

	public static final Economie ECO = new Economie();
	private final Map<OfflinePlayer, DiaCount> eco = new HashMap<>();
	private DiaCount total;

	private Economie() {
		load();
	}

	public void subtract(DiaCount diaCount, OfflinePlayer player) {
		total.subtract(diaCount);
		Optional.ofNullable(eco.get(player)).ifPresentOrElse(d -> d.subtract(diaCount),
				() -> log.warn("Can't subtract {} from {}", diaCount, player.getName()));
	}

	public void transfer(DiaCount diaCount, OfflinePlayer from, OfflinePlayer to) {
		Optional.ofNullable(eco.get(from)).ifPresentOrElse(d -> d.subtract(diaCount),
				() -> log.warn("Can't subtract {} from {}", diaCount, from.getName()));
		Optional.ofNullable(eco.get(to)).ifPresentOrElse(d -> d.add(diaCount), () -> eco.put(to, diaCount));
	}

	public void add(DiaCount diaCount, OfflinePlayer player) {
		total.add(diaCount);
		Optional.ofNullable(eco.get(player)).ifPresentOrElse(d -> d.add(diaCount), () -> eco.put(player, diaCount));
	}

	public void load() {
		try {
			Class.forName("io.github.mcrtin.diaWatcher.DiaCount");
		} catch (ClassNotFoundException e) {
			return;
		}
		final FileConfiguration config = Main.getPlugin().getConfig();
		total = config.getSerializable("total", DiaCount.class, new DiaCount());
		final ConfigurationSection configurationSection = config.getConfigurationSection("eco");
		if (configurationSection != null)
			configurationSection.getValues(true)
					.forEach((s, o) -> eco.put(Bukkit.getOfflinePlayer(UUID.fromString(s)), (DiaCount) o));
		updateTotal();
	}

	public void save() {
		updateTotal();
		final FileConfiguration config = Main.getPlugin().getConfig();
		config.set("total", total);
		eco.forEach((player, diaCount) -> config.set("eco." + player.getUniqueId(), diaCount));
		Main.getPlugin().saveConfig();
	}

	private void updateTotal() {
		DiaCount diaCount = new DiaCount();
		eco.values().forEach(diaCount::add);
		if (diaCount.equals(total))
			return;
		log.warn("Something went wrong. Total value is not right: expected {}, but got {}", total, diaCount);
		total = diaCount;
	}

}
