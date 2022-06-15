package io.github.mcrtin.reflections;

public class NmsClass {

	public static Class<?> getNmsClass(String name) throws ClassNotFoundException {
		return Class.forName(name.replace("%version%", Version.getNmsVersion()));
	}
}
