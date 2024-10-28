package net.devmc.thermite.lib.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.impl.controller.*;
import net.devmc.thermite.lib.config.ConfigFile;
import net.devmc.thermite.lib.config.ConfigScreen;
import net.devmc.thermite.lib.config.types.ColorWrapper;
import net.devmc.thermite.lib.config.types.PrimitiveWrapper;
import net.devmc.thermite.lib.config.util.JsonSerializable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class YaclConfigScreen extends ConfigScreen {

	private final ConfigFile configFile;

	public YaclConfigScreen(ConfigFile configFile) {
		super(configFile);
		this.configFile = configFile;
	}

	@Override
	public Screen createConfigScreen(Screen parent) {
		YetAnotherConfigLib.Builder configBuilder = YetAnotherConfigLib.createBuilder();
		configBuilder.title(Text.translatable("title.%s.config", configFile.mod.getModId()));

		Map<String, ConfigCategory.Builder> categories = new HashMap<>();

		for (Map.Entry<String, Map<String, JsonSerializable>> categoryEntry : configFile.values.entrySet()) {
			String categoryKey = categoryEntry.getKey();
			Map<String, JsonSerializable> categoryValues = categoryEntry.getValue();

			ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder();
			categoryBuilder.name(Text.translatable(String.format("category.%s.config.%s", configFile.mod.getModId(), categoryKey)));
			categories.put(categoryKey, categoryBuilder);

			OptionGroup.Builder groupBuilder = OptionGroup.createBuilder();

			for (Map.Entry<String, JsonSerializable> entry : categoryValues.entrySet()) {
				String key = entry.getKey();
				JsonSerializable value = entry.getValue();

				if (value instanceof PrimitiveWrapper primitiveWrapper) {
					addPrimitiveField(groupBuilder, categoryKey, key, primitiveWrapper);
				} else if (value instanceof ColorWrapper colorWrapper) {
					addColorField(groupBuilder, categoryKey, key, colorWrapper);
				}
			}

			categoryBuilder.group(groupBuilder.build());
			configBuilder.category(categoryBuilder.build());
		}

		// Add all categories to the config builder
		categories.values().forEach(category -> configBuilder.category(category.build()));

		// Save the config when the screen is closed
		configBuilder.save(configFile::save);

		return configBuilder.build().generateScreen(parent);
	}

	protected void addPrimitiveField(OptionGroup.Builder builder, String categoryKey, String key, PrimitiveWrapper primitiveWrapper) {
		Object primitiveValue = primitiveWrapper.getValue();

		if (primitiveValue instanceof Integer integer) {
			addIntegerField(builder, categoryKey, key, integer);
		} else if (primitiveValue instanceof Double d) {
			addDoubleField(builder, categoryKey, key, d);
		} else if (primitiveValue instanceof Float f) {
			addFloatField(builder, categoryKey, key, f);
		} else if (primitiveValue instanceof Long l) {
			addLongField(builder, categoryKey, key, l);
		} else if (primitiveValue instanceof Boolean bool) {
			addBooleanField(builder, categoryKey, key, bool);
		} else if (primitiveValue instanceof String string) {
			addStringField(builder, categoryKey, key, string);
		}
	}

	protected void addIntegerField(OptionGroup.Builder builder, String categoryKey, String key, int value) {
		builder.option(Option.<Integer>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getInteger(categoryKey, key).orElse(value),
						newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue))
				)
				.controller(IntegerFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addLongField(OptionGroup.Builder builder, String categoryKey, String key, long value) {
		builder.option(Option.<Long>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getLong(categoryKey, key).orElse(value),
						newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue))
				)
				.controller(LongFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addFloatField(OptionGroup.Builder builder, String categoryKey, String key, float value) {
		builder.option(Option.<Float>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getFloat(categoryKey, key).orElse(value),
						newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue))
				)
				.controller(FloatFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addDoubleField(OptionGroup.Builder builder, String categoryKey, String key, double value) {
		builder.option(Option.<Double>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getDouble(categoryKey, key).orElse(value),
						newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue))
				)
				.controller(DoubleFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addBooleanField(OptionGroup.Builder builder, String categoryKey, String key, boolean value) {
		builder.option(Option.<Boolean>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getBoolean(categoryKey, key).orElse(value),
						newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue))
				)
				.controller(BooleanControllerBuilderImpl::new)
				.build());
	}

	protected void addStringField(OptionGroup.Builder builder, String categoryKey, String key, String value) {
		builder.option(Option.<String>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getString(categoryKey, key).orElse(value),
						newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue))
				)
				.controller(StringControllerBuilderImpl::new)
				.build());
	}

	protected void addColorField(OptionGroup.Builder builder, String categoryKey, String key, ColorWrapper value) {
		builder.option(Option.<Integer>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						(Integer) value.getValue(),
						() -> configFile.getColor(categoryKey, key).map(c -> (Integer) c.getValue()).orElse((Integer) value.getValue()),
						newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue))
				)
				.controller(IntegerFieldControllerBuilderImpl::new)
				.build());
	}
}
