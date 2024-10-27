package net.devmc.thermite.lib.config;

import com.google.gson.*;
import net.devmc.thermite.lib.Mod;
import net.devmc.thermite.lib.config.util.JsonSerializable;
import net.devmc.thermite.lib.config.types.PrimitiveWrapper;
import net.devmc.thermite.lib.config.util.EnumSerializer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused") // I hate warnings
public class ConfigFile {

	public final Mod mod;
	private final File file;
	private final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Enum.class, new EnumSerializer() {})
			.create();
	public final Map<String, JsonSerializable> values = new HashMap<>();
	private final Map<String, JsonSerializable> defaultValues = new HashMap<>();

	public ConfigFile(Mod mod) {
		this(mod, new File("config/" + mod.getModId() + ".json"));
	}

	public ConfigFile(Mod mod, File file) {
		this.mod = mod;
		this.file = file;
		try {
			if (!file.exists() && !file.createNewFile())
				mod.getLogger().error("Failed to create config file for mod {}", mod.getModId());
		} catch (IOException e) {
			mod.getLogger().error("Error creating config file", e);
		}
	}

	public void load() {
		if (!file.exists()) {
			mod.getLogger().warn("ConfigFile file for mod {} does not exist. Creating file...", mod.getModId());
			try {
				file.createNewFile();
			} catch (IOException e) {
				mod.getLogger().error("Failed to create config file for mod {}", mod.getModId());
			}
			values.putAll(defaultValues);
			save();
		}

		try (FileReader reader = new FileReader(file)) {
			JsonElement jsonElement = JsonParser.parseReader(reader);
			if (!jsonElement.isJsonObject()) {
				mod.getLogger().error("ConfigFile file is not a valid JSON object: {}", file.getPath());
                values.putAll(defaultValues);
				save();
			}
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				String key = entry.getKey();
				JsonElement element = entry.getValue();
				if (defaultValues.containsKey(key)) {
					try {
						JsonSerializable value = JsonSerializableRegistry.create(key, element);
						values.put(key, value);
					} catch (Exception e) {
						mod.getLogger().warn("Failed to deserialize key {}: {}", key, e.getMessage());
					}
				}
			}
		} catch (IOException | JsonParseException e) {
			mod.getLogger().error("Failed to load config file: {}", file.getPath(), e);
			values.putAll(defaultValues);
		}

	}

	public void save() {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, JsonSerializable> entry : values.entrySet()) {
			jsonObject.add(entry.getKey(), entry.getValue().serialize());
		}

		try (FileWriter fileWriter = new FileWriter(file)) {
			GSON.toJson(jsonObject, fileWriter);
		} catch (IOException e) {
			mod.getLogger().error("Failed to save config file: {}", file.getPath(), e);
			throw new RuntimeException("Failed to save config file", e);
		}
	}

	public <T extends JsonSerializable> void setDefault(String key, T value) {
		defaultValues.put(key, value);
		values.putIfAbsent(key, value);
		if (!JsonSerializableRegistry.isRegistered(key)) {
			JsonSerializableRegistry.register(key, value.getClass());
		}
	}

	// Type-safe getters
	public Integer getInteger(String key) {
		return getPrimitiveValue(key, Integer.class);
	}

	public Float getFloat(String key) {
		return getPrimitiveValue(key, Float.class);
	}

	public Double getDouble(String key) {
		return getPrimitiveValue(key, Double.class);
	}

	public Boolean getBoolean(String key) {
		return getPrimitiveValue(key, Boolean.class);
	}

	public String getString(String key) {
		return getPrimitiveValue(key, String.class);
	}

	public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
		JsonSerializable value = values.get(key);
		if (value instanceof PrimitiveWrapper primitiveWrapper) {
			Object wrappedValue = primitiveWrapper.getValue();
			if (wrappedValue instanceof Integer ordinal) {
				try {
					return enumClass.getEnumConstants()[ordinal];
				} catch (ArrayIndexOutOfBoundsException e) {
					mod.getLogger().warn("Invalid enum ordinal for key {}: {}. Using default: {}", key, ordinal, defaultValue);
					return defaultValue;
				}
			}
		}
		mod.getLogger().warn("No enum value found for key {}. Using default: {}", key, defaultValue);
		return defaultValue;
	}

	public void set(String key, PrimitiveWrapper value) {
		values.put(key, value);

	}

	@SuppressWarnings("unchecked")
	public <T> T getPrimitiveValue(String key, Class<T> clazz) {
		JsonSerializable value = values.get(key);
		if (value instanceof PrimitiveWrapper primitiveWrapper) {
			Object wrappedValue = primitiveWrapper.getValue();

			// Enum handling
			if (clazz.isEnum() && wrappedValue instanceof String) {
				@SuppressWarnings("unchecked")
				T enumValue = (T) Enum.valueOf((Class<? extends Enum>) clazz, (String) wrappedValue);
				return enumValue;
			}

			// Primitive type handling
			if (clazz.isInstance(wrappedValue)) {
				return (T) wrappedValue;
			}
		}
		throw new ClassCastException("Config value for key " + key + " is not of type " + clazz.getSimpleName());
	}

	public static class Builder {
		private Mod mod;
		private File file;

		public Builder mod(Mod mod) {
			this.mod = mod;
			return this;
		}

		public Builder file(File file) {
			this.file = file;
			return this;
		}

		public ConfigFile build() {
			if (mod == null) {
				throw new IllegalStateException("Mod must be provided");
			}
			if (file == null) {
				file = new File("config/" + mod.getModId());
			}
			return new ConfigFile(mod, file);
		}
	}
}
