package net.devmc.thermite.lib.config.util;

import com.google.gson.JsonElement;

public interface JsonSerializable {
	JsonElement serialize();

	void deserialize(JsonElement jsonElement, Class<?> targetType);

	Object getValue();

	static <T extends JsonSerializable> T fromJson(JsonElement jsonElement, Class<T> clazz) {
		try {
			T instance = clazz.getDeclaredConstructor().newInstance();
			instance.deserialize(jsonElement, clazz);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize " + clazz.getSimpleName(), e);
		}
	}
}
