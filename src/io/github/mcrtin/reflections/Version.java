package io.github.mcrtin.reflections;

import javax.inject.Singleton;

import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Singleton
@AllArgsConstructor
public class Version {
	private static final Version VERSION = new Version(getVERSION());
	@Getter
	private final String versionString;

	public static String getNmsVersion() {
		return VERSION.getVersionString();
	}

	private static String getVERSION() {
		String v = Bukkit.getServer().getClass().getPackage().getName();
		return v.substring(v.lastIndexOf('.') + 1);
	}
}
