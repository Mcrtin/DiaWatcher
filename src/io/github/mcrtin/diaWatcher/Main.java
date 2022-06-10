package io.github.mcrtin.diaWatcher;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.mcrtin.logToPlayers.DiaFeedback;

public class Main extends JavaPlugin {
	private static JavaPlugin plugin;

	public void onEnable() {
		plugin = this;
		ConfigurationSerialization.registerClass(DiaCount.class);
		Bukkit.getPluginManager().registerEvents(new DiaListener(), this);
		getCommand("sendDiaFeedback").setExecutor(new DiaFeedback());
	}

	public void onDisable() {
		Econemie.ECO.save();
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}

}
