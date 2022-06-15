package io.github.mcrtin.reflections;

import java.lang.reflect.Field;
import java.util.Arrays;

public class NmsField {

	public static Field getNmsField(Class<?> clazz, String... possibleNames)
			throws NoSuchFieldException, SecurityException {
		return Arrays.stream(clazz.getDeclaredFields()).filter(
				f -> f.getType().equals(int.class) && Arrays.stream(possibleNames).anyMatch(f.getName()::equals))
				.findFirst().orElseThrow(() -> new NoSuchFieldException());
	}

	public static Field getNmsField(String nmsClass, String... possibleNames)
			throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		return getNmsField(NmsClass.getNmsClass(nmsClass), possibleNames);
	}
}
