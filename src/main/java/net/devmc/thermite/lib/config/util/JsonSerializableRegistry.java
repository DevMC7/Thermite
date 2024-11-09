package net.devmc.thermite.lib.config.util;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class JsonSerializableRegistry {

	private static final Map<String, Class<? extends JsonSerializable>> registry = new HashMap<>();
	private static final Map<String, BiFunction<String, JsonElement, JsonSerializable>> customDeserializers = new HashMap<>();

	public static void register(String key, Class<? extends JsonSerializable> clazz) {
		registry.put(key, clazz);
	}

	public static void registerCustomDeserializer(String key, BiFunction<String, JsonElement, JsonSerializable> deserializer) {
		customDeserializers.put(key, deserializer);
	}

	public static boolean isRegistered(String key) {
		return registry.containsKey(key);
	}

	public static Optional<Class<? extends JsonSerializable>> get(String key) {
		return Optional.ofNullable(registry.get(key));
	}

	public static JsonSerializable create(String key, JsonElement jsonElement) {
		if (customDeserializers.containsKey(key)) {
			return customDeserializers.get(key).apply(key, jsonElement);
		}
		return get(key)
				.map(clazz -> JsonSerializable.fromJson(jsonElement, clazz))
				.orElseThrow(() -> new IllegalArgumentException("No registered serializable type for key: " + key));
	}

	public static JsonSerializable createFromClass(Class<? extends JsonSerializable> clazz, JsonElement jsonElement) {
		try {
			JsonSerializable instance = clazz.getDeclaredConstructor().newInstance();
			instance.deserialize(jsonElement, clazz);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate or deserialize class: " + clazz.getName(), e);
		}
	}

}
