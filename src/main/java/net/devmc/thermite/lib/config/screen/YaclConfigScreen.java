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

import java.util.Map;
import java.util.Optional;

public class YaclConfigScreen extends ConfigScreen {

	private final ConfigFile configFile;

	public YaclConfigScreen(ConfigFile configFile) {
		super(configFile);
		this.configFile = configFile;
	}

	@Override
	public Screen createConfigScreen(Screen parent) {
		YetAnotherConfigLib.Builder configBuilder = YetAnotherConfigLib.createBuilder();
		ConfigCategory.Builder configCategoryBuilder = ConfigCategory.createBuilder();
		OptionGroup.Builder builder = OptionGroup.createBuilder();

		configBuilder.title(Text.translatable("title.%s.config", configFile.mod.getModId()));

		for (Map.Entry<String, JsonSerializable> entry : configFile.values.entrySet()) {
			String key = entry.getKey();
			JsonSerializable value = entry.getValue();

			if (value instanceof PrimitiveWrapper primitiveWrapper) {
				Object primitiveValue = primitiveWrapper.getValue();

				if (primitiveValue instanceof Number number) {
					switch (number) {
						case Integer integer -> addIntegerField(builder, key, integer);
						case Double d -> addDoubleField(builder, key, d);
						case Float f -> addFloatField(builder, key, f);
						default -> addLongField(builder, key, number.longValue());
					}
				} else if (primitiveValue instanceof Boolean bool) {
					addBooleanField(builder, key, bool);
				} else if (primitiveValue instanceof String string) {
					addStringField(builder, key, string);
				}
			} else if (value instanceof ColorWrapper colorWrapper) {
				addColorField(builder, key, colorWrapper);
			}
		}

		configCategoryBuilder.group(builder.build());
		configCategoryBuilder.name(Text.translatable(String.format("category.%s.config", configFile.mod.getModId())));
		configBuilder.category(configCategoryBuilder.build());

		// Save the config when the screen is closed
		configBuilder.save(configFile::save);

		return configBuilder.build().generateScreen(parent);
	}

	protected void addIntegerField(OptionGroup.Builder builder, String key, int value) {
		builder.option(Option.<Integer>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getInteger(key).orElseGet(() -> value),
						newValue -> configFile.set(key, new PrimitiveWrapper(newValue))
				)
				.controller(IntegerFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addDoubleField(OptionGroup.Builder builder, String key, double value) {
		builder.option(Option.<Double>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getDouble(key).orElseGet(() -> value),
						newValue -> configFile.set(key, new PrimitiveWrapper(newValue))
				)
				.controller(DoubleFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addFloatField(OptionGroup.Builder builder, String key, float value) {
		builder.option(Option.<Float>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getFloat(key).orElseGet(() -> value),
						newValue -> configFile.set(key, new PrimitiveWrapper(newValue))
				)
				.controller(FloatFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addLongField(OptionGroup.Builder builder, String key, long value) {
		builder.option(Option.<Long>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getLong(key).orElseGet(() -> value),
						newValue -> configFile.set(key, new PrimitiveWrapper(newValue))
				)
				.controller(LongFieldControllerBuilderImpl::new)
				.build());
	}

	protected void addBooleanField(OptionGroup.Builder builder, String key, boolean value) {
		builder.option(Option.<Boolean>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getBoolean(key).orElseGet(() -> value),
						newValue -> configFile.set(key, new PrimitiveWrapper(newValue))
				)
				.controller(BooleanControllerBuilderImpl::new)
				.build());
	}

	protected void addStringField(OptionGroup.Builder builder, String key, String value) {
		builder.option(Option.<String>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						value,
						() -> configFile.getString(key).orElseGet(() -> value),
						newValue -> configFile.set(key, new PrimitiveWrapper(newValue))
				)
				.controller(StringControllerBuilderImpl::new)
				.build());
	}

	protected void addColorField(OptionGroup.Builder builder, String key, ColorWrapper value) {
		builder.option(Option.<Integer>createBuilder()
				.name(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)))
				.binding(
						(Integer) value.getValue(),
						() -> {
							Optional<ColorWrapper> color = configFile.getColor(key);
							return (Integer) (color.isPresent()? color.get().getValue() : value.getValue());
							},
						newValue -> configFile.set(key, new PrimitiveWrapper(newValue))
				)
				.controller(IntegerFieldControllerBuilderImpl::new)
				.build());
	}
}
