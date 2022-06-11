package io.github.mcrtin.diaWatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

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

	public DiaCount(ItemStack itemStack) {
		this();
		add(itemStack);
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

	public void mulitply(int muiltplyer) {
		raw *= muiltplyer;
		ore *= muiltplyer;
	}

	public String toString() {
		return raw + " dias and " + ore + " ores";
	}

	public void add(ItemStack itemStack) {
		if (itemStack == null)
			return;
		switch (itemStack.getType()) {
		case DIAMOND:
			add(itemStack.getAmount());
			return;
		case DIAMOND_ORE:
			addOre(itemStack.getAmount());
			return;
		case DIAMOND_BLOCK:
			add(itemStack.getAmount() * 9);
			return;
		case SHULKER_BOX:
			final ItemMeta itemMeta = itemStack.getItemMeta();
			if (!(itemMeta instanceof BlockStateMeta))
				return;
			BlockStateMeta im = (BlockStateMeta) itemMeta;
			if ((im.getBlockState() instanceof Container))
				return;
			Container container = (Container) im.getBlockState();
			final Inventory snapshotInventory = container.getSnapshotInventory();
			snapshotInventory.forEach(this::add);

		default:
			return;
		}
	}

	public void negate() {
		raw = -raw;
		ore = -ore;
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
		if (raw == null || !raw.getClass().equals(int.class))
			raw = 0;
		Object ore = args.get("ore");
		if (ore == null || !ore.getClass().equals(int.class))
			ore = 0;
		return new DiaCount((int) raw, (int) ore);
	}

	public DiaCount(Material type, int i) {
		switch (type) {
		case DIAMOND:
			add(i);
			return;
		case DIAMOND_ORE:
			addOre(i);
			return;
		case DIAMOND_BLOCK:
			add(i * 9);
			return;
		default:
			return;
		}
	}

}
