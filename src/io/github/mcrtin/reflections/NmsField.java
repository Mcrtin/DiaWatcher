package io.github.mcrtin.reflections;

import java.lang.reflect.Field;
import java.util.Arrays;

public class NmsField {

	public static Field getNmsField(Class<?> clazz, String... possibleNames)
			throws NoSuchFieldException, SecurityException {
		return Arrays.stream(clazz.getDeclaredFields()).filter(
				f -> f.getType() == int.class && Arrays.asList(possibleNames).contains(f.getName()))
				.findFirst().orElseThrow(NoSuchFieldException::new);
	}

	public static Field getNmsField(String nmsClass, String... possibleNames)
			throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		return getNmsField(NmsClass.getNmsClass(nmsClass), possibleNames);
	}
}
