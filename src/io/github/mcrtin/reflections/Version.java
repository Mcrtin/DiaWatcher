package io.github.mcrtin.reflections;

import javax.inject.Singleton;

import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Singleton
@AllArgsConstructor
public class Version {
	private static Version version = new Version(getVersion());
	@Getter
	private final String versionString;

	public static String getNmsVersion() {
		return version.getVersionString();
	}

	private static String getVersion() {
		String v = Bukkit.getServer().getClass().getPackage().getName();
		return v.substring(v.lastIndexOf('.') + 1);
	}
}
