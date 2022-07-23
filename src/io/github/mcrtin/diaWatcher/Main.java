package io.github.mcrtin.diaWatcher;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.jeff_media.customblockdata.CustomBlockData;

import io.github.mcrtin.logToPlayers.DiaFeedback;

import java.util.Objects;

public class Main extends JavaPlugin {
	private static JavaPlugin plugin;

	public void onEnable() {
		plugin = this;
		try {
			NmsItem.init();
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException e) {
			getLogger().severe("This API-version is not suported - disabling.");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
			return;
		}
		CustomBlockData.registerListener(this);
		ConfigurationSerialization.registerClass(DiaCount.class);
		Bukkit.getPluginManager().registerEvents(new DiaListener(), this);
		Objects.requireNonNull(getCommand("sendDiaFeedback")).setExecutor(new DiaFeedback());

	}

	public void onDisable() {
		Economie.ECO.save();
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}

}
