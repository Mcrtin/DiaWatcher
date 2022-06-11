package io.github.mcrtin.diaWatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import com.jeff_media.customblockdata.CustomBlockData;

import io.github.mcrtin.logToPlayers.DiaFeedback;

public class Main extends JavaPlugin {
	private static JavaPlugin plugin;
	private static Field itemHealth;
	private static Class<?> craftItem;
	private static Method getHandle;
	public void onEnable() {
		plugin = this;
		try {
			craftItem = Class.forName("craftbukkit." + getNMSVersion() + ".entity.CraftItem");
			getHandle = craftItem.getMethod("getHandle");
			itemHealth = getItemHealthField();
			CustomBlockData.registerListener(this);
			ConfigurationSerialization.registerClass(DiaCount.class);
			Bukkit.getPluginManager().registerEvents(new DiaListener(), this);
			getCommand("sendDiaFeedback").setExecutor(new DiaFeedback());
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException e) {
			getLogger().severe("This API-version is not suported - disabling.");
			Bukkit.getPluginManager().disablePlugin(this);
		}

	}

	private Field getItemHealthField() throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		String[] possibleNames = new String[] { "health", "f", "e" };
		Class<?> clazz = Class.forName("net.minecraft.server." + getNMSVersion() + ".EntityItem");
		Stream<String> stream = Arrays.stream(possibleNames);
		Field field = Arrays.stream(clazz.getFields()).filter(f -> f.getType().equals(int.class))
				.filter(f -> stream.anyMatch(f.getName()::equals)).findFirst()
				.orElseThrow(() -> new NoSuchFieldException());
		field.setAccessible(true);
		return field;
	}

	public static String getNMSVersion() {
		String v = Bukkit.getServer().getClass().getPackage().getName();
		return v.substring(v.lastIndexOf('.') + 1);
	}

	public void onDisable() {
		Econemie.ECO.save();
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}

	public static Object getNMSItem(Item item) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return getHandle.invoke(craftItem.cast(item));
	}

	public static int getHealth(Item item) {
		try {
			return itemHealth.getInt(getNMSItem(item));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			plugin.getLogger().severe("Something went wrong while getting the item health - disabling.");
			Bukkit.getPluginManager().disablePlugin(plugin);
			return 0;
		}
	}

}
