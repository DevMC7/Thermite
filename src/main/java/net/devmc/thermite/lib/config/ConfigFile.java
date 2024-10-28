package net.devmc.thermite.lib.config;

import com.google.gson.*;
import net.devmc.thermite.lib.Mod;
import net.devmc.thermite.lib.config.types.ArrayWrapper;
import net.devmc.thermite.lib.config.util.JsonSerializable;
import net.devmc.thermite.lib.config.types.ColorWrapper;
import net.devmc.thermite.lib.config.types.PrimitiveWrapper;
import net.devmc.thermite.lib.config.util.JsonSerializableRegistry;

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class ConfigFile {

	private final String name;
	public final Mod mod;
	private final File file;
	private Gson GSON;
	private boolean prettyPrint;
	public final Map<String, Map<String, JsonSerializable>> values = new HashMap<>();
	private final Map<String, Map<String, JsonSerializable>> defaultValues = new HashMap<>();

	public ConfigFile(Mod mod) {
		this(mod.getModId(), mod, new File(String.format("config/%s.json", mod.getModId())));
	}

	public ConfigFile(String name, Mod mod) {
		this(name, mod, new File(String.format("config/%s.%s.json", name, mod.getModId())));
	}

	public ConfigFile(Mod mod, File file) {
		this(file.getName(), mod, file);
	}

	public ConfigFile(String name, Mod mod, File file) {
		this.name = name;
		this.mod = mod;
		this.file = file;
		setPrettyPrint(true);
		initializeFile();
	}

	public String getName() {
		return this.name;
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
				for (Map.Entry<String, JsonElement> categoryEntry : jsonObject.entrySet()) {
					String category = categoryEntry.getKey();
					JsonObject categoryObject = categoryEntry.getValue().getAsJsonObject();
					Map<String, JsonSerializable> categoryValues = new HashMap<>();
					for (Map.Entry<String, JsonElement> entry : categoryObject.entrySet()) {
						String key = entry.getKey();
						JsonElement element = entry.getValue();
						if (defaultValues.containsKey(category) && defaultValues.get(category).containsKey(key)) {
							try {
								JsonSerializable value = JsonSerializableRegistry.create(key, element);
								categoryValues.put(key, value);
							} catch (Exception e) {
								mod.getLogger().warn("Failed to deserialize key {} in category {}: {}", key, category, e.getMessage());
							}
						}
					}
					values.put(category, categoryValues);
				}
			}
		} catch (IOException | JsonParseException e) {
			mod.getLogger().error("Failed to load config file: {}", file.getPath(), e);
			values.putAll(defaultValues);
		}
	}

	public void save() {
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Map<String, JsonSerializable>> categoryEntry : values.entrySet()) {
			JsonObject categoryObject = new JsonObject();
			for (Map.Entry<String, JsonSerializable> entry : categoryEntry.getValue().entrySet()) {
				categoryObject.add(entry.getKey(), entry.getValue().serialize());
			}
			jsonObject.add(categoryEntry.getKey(), categoryObject);
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
		setDefault(mod.getDefaultConfigCategory(), key, value);
	}

	public <T extends JsonSerializable> void setDefault(String category, String key, T value) {
		defaultValues.computeIfAbsent(category, k -> new HashMap<>()).put(key, value);
		values.computeIfAbsent(category, k -> new HashMap<>()).putIfAbsent(key, value);
		if (!JsonSerializableRegistry.isRegistered(key)) {
			JsonSerializableRegistry.register(key, value.getClass());
		}
	}

	public void set(String key, JsonSerializable value) {
		set(mod.getDefaultConfigCategory(), key, value);
	}

	public void set(String category, String key, JsonSerializable value) {
		values.computeIfAbsent(category, k -> new HashMap<>()).put(key, value);
	}

	public Optional<JsonSerializable> get(String key) {
		return get(mod.getDefaultConfigCategory(), key);
	}

	public Optional<JsonSerializable> get(String category, String key) {
		return Optional.ofNullable(values.getOrDefault(category, new HashMap<>()).get(key));
	}

	public void resetToDefault(String key) {
		resetToDefault(mod.getDefaultConfigCategory(), key);
	}

	public void resetToDefault(String category, String key) {
		if (defaultValues.containsKey(category) && defaultValues.get(category).containsKey(key)) {
			values.get(category).put(key, defaultValues.get(category).get(key));
		} else {
			values.get(category).remove(key);
		}
		save();
	}

	public void remove(String key) {
		remove(mod.getDefaultConfigCategory(), key);
	}

	public void remove(String category, String key) {
		if (values.containsKey(category)) {
			values.get(category).remove(key);
		}
		if (defaultValues.containsKey(category)) {
			defaultValues.get(category).remove(key);
		}
		save();
	}

	// Type-safe getters with categories
	public Optional<Integer> getInteger(String key) {
		return getInteger(mod.getDefaultConfigCategory(), key);
	}

	public Optional<Integer> getInteger(String category, String key) {
		return getPrimitiveValue(category, key, Integer.class);
	}

	public Optional<Float> getFloat(String key) {
		return getFloat(mod.getDefaultConfigCategory(), key);
	}

	public Optional<Float> getFloat(String category, String key) {
		return getPrimitiveValue(category, key, Float.class);
	}

	public Optional<Double> getDouble(String key) {
		return getDouble(mod.getDefaultConfigCategory(), key);
	}

	public Optional<Double> getDouble(String category, String key) {
		return getPrimitiveValue(category, key, Double.class);
	}

	public Optional<Long> getLong(String key) {
		return getLong(mod.getDefaultConfigCategory(), key);
	}

	public Optional<Long> getLong(String category, String key) {
		return getPrimitiveValue(category, key, Long.class);
	}

	public Optional<Boolean> getBoolean(String key) {
		return getBoolean(mod.getDefaultConfigCategory(), key);
	}

	public Optional<Boolean> getBoolean(String category, String key) {
		return getPrimitiveValue(category, key, Boolean.class);
	}

	public Optional<String> getString(String key) {
		return getString(mod.getDefaultConfigCategory(), key);
	}

	public Optional<String> getString(String category, String key) {
		return getPrimitiveValue(category, key, String.class);
	}

	public Optional<List> getList(String key) {
		return getList(mod.getDefaultConfigCategory(), key);
	}

	public Optional<List> getList(String category, String key) {
		JsonSerializable value = values.getOrDefault(category, new HashMap<>()).get(key);
		if (value instanceof ArrayWrapper arrayWrapper) {
			return Optional.of(arrayWrapper.getElements());
		}
		return Optional.empty();
	}

	public Optional<ColorWrapper> getColor(String key) {
		return getColor(mod.getDefaultConfigCategory(), key);
	}

	public Optional<ColorWrapper> getColor(String category, String key) {
		JsonSerializable value = values.getOrDefault(category, new HashMap<>()).get(key);
		if (value instanceof ColorWrapper colorWrapper) {
			return Optional.of(colorWrapper);
		}
		return Optional.empty();
	}

	public void setColor(String key, ColorWrapper value) {
		values.computeIfAbsent(mod.getDefaultConfigCategory(), k -> new HashMap<>()).put(key, value);
	}

	public void setColor(String category, String key, ColorWrapper value) {
		values.computeIfAbsent(category, k -> new HashMap<>()).put(key, value);
	}

	public <T> Optional<T> getPrimitiveValue(String category, String key, Class<T> clazz) {
		JsonSerializable value = values.getOrDefault(category, new HashMap<>()).get(key);
		if (value instanceof PrimitiveWrapper primitiveWrapper) {
			Object wrappedValue = primitiveWrapper.getValue();
			if (clazz.isInstance(wrappedValue)) {
				return Optional.of(clazz.cast(wrappedValue));
			}
		}
		return Optional.empty();
	}

	public static class Builder {
		private String name;
		private Mod mod;
		private File file;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder mod(Mod mod) {
			this.mod = mod;
			return this;
		}

		public Builder file(File file) {
			this.file = file;
			return this;
		}

		public ConfigFile build() {
			if (mod == null) throw new IllegalStateException("Mod must be provided");
			if (name == null) name = mod.getModId();
			if (file == null) file = new File("config/" + mod.getModId() + ".json");
			return new ConfigFile(name, mod, file);
		}
	}
}
