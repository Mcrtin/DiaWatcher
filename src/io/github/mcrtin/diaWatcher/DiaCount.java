package io.github.mcrtin.diaWatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiaCount implements ConfigurationSerializable {

	private int raw;
	private int ore;

	public DiaCount() {
		this(0, 0);
	}

	public void add(DiaCount diaCount) {
		raw += diaCount.getRaw();
		ore += diaCount.getOre();
	}

	public void subtract(DiaCount diaCount) {
		raw -= diaCount.getRaw();
		ore -= diaCount.getOre();
		assert raw >= 0;
		assert ore >= 0;
	}

	public void add(int raw) {
		this.raw += raw;
	}

	public void addOre(int ore) {
		this.ore += ore;
	}

	public boolean isEmpty() {
		return raw == 0 && ore == 0;
	}

	public String toString() {
		return raw + " dias and " + ore + " ores";
	}

	@Override
	@Nonnull
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new LinkedHashMap<>();
		result.put("raw", raw);
		result.put("ore", ore);
		return result;
	}

	@Nonnull
	public static DiaCount deserialize(Map<String, Object> args) {
		Object raw = args.get("raw");
		if (!(raw instanceof Integer))
			raw = 0;
		Object ore = args.get("ore");
		if (!(ore instanceof Integer))
			ore = 0;
		return new DiaCount((int) raw, (int) ore);
	}

	public DiaCount(Material type, int i) {
		switch (type) {
			case DIAMOND -> add(i);
			case DIAMOND_ORE -> addOre(i);
			case DIAMOND_BLOCK -> add(i * 9);
		}
	}

}
