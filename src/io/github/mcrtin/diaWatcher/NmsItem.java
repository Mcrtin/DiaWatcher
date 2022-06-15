package io.github.mcrtin.diaWatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

import io.github.mcrtin.reflections.NmsClass;
import io.github.mcrtin.reflections.NmsField;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NmsItem {
	private static Field itemHealth;
	private static Class<?> craftItem;
	private static Method getHandle;
	
	static void init() throws NoSuchMethodException, SecurityException, ClassNotFoundException, NoSuchFieldException {
		craftItem = NmsClass.getNmsClass("org.bukkit.craftbukkit.%version%.entity.CraftItem");
		getHandle = craftItem.getMethod("getHandle");
		itemHealth = NmsField.getNmsField("net.minecraft.server.%version%.EntityItem", "health", "f", "e");
	
	}
	public static int getHealth(Item item) {
		try {
			return itemHealth.getInt(getHandle.invoke(craftItem.cast(item)));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			log.error("Something went wrong while getting the item health - disabling.");
			Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
			return 0;
		}
	}
}
