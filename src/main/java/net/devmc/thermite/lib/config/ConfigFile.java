package net.devmc.thermite.lib.config;

import com.google.gson.*;
import net.devmc.thermite.lib.Mod;
import net.devmc.thermite.lib.config.util.JsonSerializable;
import net.devmc.thermite.lib.config.types.ColorWrapper;
import net.devmc.thermite.lib.config.types.PrimitiveWrapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused") // I hate warnings
public class ConfigFile {

	public final Mod mod;
	private final File file;
	private Gson GSON;
	private boolean prettyPrint;
	public final Map<String, JsonSerializable> values = new HashMap<>();
	private final Map<String, JsonSerializable> defaultValues = new HashMap<>();

	public ConfigFile(Mod mod) {
		this(mod, new File("config/" + mod.getModId() + ".json"));
	}

	public ConfigFile(Mod mod, File file) {
		this.mod = mod;
		this.file = file;
		setPrettyPrint(false);
		initializeFile();
	}

	private void initializeFile() {
		try {
			if (!file.exists() && !file.createNewFile()) {
				mod.getLogger().error("Failed to create config file for mod {}", mod.getModId());
			}
		} catch (IOException e) {
			mod.getLogger().error("Error creating config file", e);
		}
	}

	public void load() {
		if (!file.exists()) {
			mod.getLogger().warn("Config file for mod {} does not exist. Creating file...", mod.getModId());
			try {
				file.createNewFile();
				values.putAll(defaultValues);
				save();
			} catch (IOException e) {
				mod.getLogger().error("Failed to create config file for mod {}", mod.getModId());
			}
		}

		try (FileReader reader = new FileReader(file)) {
			JsonElement jsonElement = JsonParser.parseReader(reader);
			if (!jsonElement.isJsonObject()) {
				mod.getLogger().error("Config file is not a valid JSON object: {}", file.getPath());
				values.putAll(defaultValues);
				save();
			} else {
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

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
		this.GSON = prettyPrint ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
	}

	public <T extends JsonSerializable> void setDefault(String key, T value) {
		defaultValues.put(key, value);
		values.putIfAbsent(key, value);
		if (!JsonSerializableRegistry.isRegistered(key)) {
			JsonSerializableRegistry.register(key, value.getClass());
		}
	}

	// Type-safe getters
	public Optional<Integer> getInteger(String key) {
		return getPrimitiveValue(key, Integer.class);
	}

	public Optional<Float> getFloat(String key) {
		return getPrimitiveValue(key, Float.class);
	}

	public Optional<Double> getDouble(String key) {
		return getPrimitiveValue(key, Double.class);
	}

	public Optional<Long> getLong(String key) {
		return getPrimitiveValue(key, Long.class);
	}

	public Optional<Boolean> getBoolean(String key) {
		return getPrimitiveValue(key, Boolean.class);
	}

	public Optional<String> getString(String key) {
		return getPrimitiveValue(key, String.class);
	}

	public Optional<ColorWrapper> getColor(String key) {
		JsonSerializable value = values.get(key);
		if (value instanceof ColorWrapper colorWrapper) {
			return Optional.of(colorWrapper);
		}
		return Optional.empty();
	}

	public void set(String key, PrimitiveWrapper value) {
		values.put(key, value);
	}

	public void setColor(String key, ColorWrapper value) {
		values.put(key, value);
	}

	public Object get(String key) {
		return getPrimitiveValue(key).orElse(null);
	}

	public void remove(String key) {
		values.remove(key);
		defaultValues.remove(key);
		save();
	}

	public void resetToDefault(String key) {
		if (defaultValues.containsKey(key)) {
			values.put(key, defaultValues.get(key));
		} else {
			values.remove(key);
		}
		save();
	}

	public <T extends JsonSerializable> Optional<PrimitiveWrapper> getPrimitiveValue(String key) {
		JsonSerializable value = values.get(key);
		if (value instanceof PrimitiveWrapper primitiveWrapper) {
			return Optional.of(primitiveWrapper);
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getPrimitiveValue(String key, Class<T> clazz) {
		JsonSerializable value = values.get(key);
		if (value instanceof PrimitiveWrapper primitiveWrapper) {
			Object wrappedValue = primitiveWrapper.getValue();
			if (clazz.isInstance(wrappedValue)) {
				return Optional.of((T) wrappedValue);
			}
		}
		return Optional.empty();
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
				file = new File("config/" + mod.getModId() + ".json");
			}
			return new ConfigFile(mod, file);
		}
	}
}
