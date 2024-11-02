package net.devmc.thermite.lib.config.screen;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.math.Color;
import net.devmc.thermite.lib.config.ConfigFile;
import net.devmc.thermite.lib.config.ConfigScreen;
import net.devmc.thermite.lib.config.types.ColorWrapper;
import net.devmc.thermite.lib.config.util.JsonSerializable;
import net.devmc.thermite.lib.config.types.PrimitiveWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableTextContent;

import java.util.Map;

public class ClothConfigScreen extends ConfigScreen {

	private final ConfigFile configFile;

	public ClothConfigScreen(ConfigFile configFile) {
		super(configFile);
		this.configFile = configFile;
	}

	@Override
	public Screen createConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(MutableText.of(new TranslatableTextContent(
						String.format("category.%s.config", configFile.mod.getModId()),
						String.format("%s Config", configFile.mod.getModId()),
						TranslatableTextContent.EMPTY_ARGUMENTS)));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		// Create categories for each section
		for (Map.Entry<String, Map<String, JsonSerializable>> categoryEntry : configFile.values.entrySet()) {
			String categoryKey = categoryEntry.getKey();
			Map<String, JsonSerializable> categoryValues = categoryEntry.getValue();

			ConfigCategory category = builder.getOrCreateCategory(
					MutableText.of(new TranslatableTextContent(
							String.format("category.%s.config.%s", configFile.mod.getModId(), categoryKey),
							String.format("%s", categoryKey),
							TranslatableTextContent.EMPTY_ARGUMENTS))
			);

			for (Map.Entry<String, JsonSerializable> entry : categoryValues.entrySet()) {
				String key = entry.getKey();
				JsonSerializable value = entry.getValue();

				if (value instanceof PrimitiveWrapper primitiveWrapper) {
					addPrimitiveField(category, entryBuilder, categoryKey, key, primitiveWrapper);
				} else if (value instanceof ColorWrapper colorWrapper) {
					int color = (int) colorWrapper.getValue();
					addColorField(category, entryBuilder, categoryKey, key, Color.ofTransparent(color));
				}
			}
		}

		// Save the config when the screen is closed
		builder.setSavingRunnable(configFile::save);

		return builder.build();
	}

	protected void addPrimitiveField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, PrimitiveWrapper primitiveWrapper) {
		Object primitiveValue = primitiveWrapper.getValue();

		if (primitiveValue instanceof Integer integer) {
			addIntegerField(category, entryBuilder, categoryKey, key, integer);
		} else if (primitiveValue instanceof Double d) {
			addDoubleField(category, entryBuilder, categoryKey, key, d);
		} else if (primitiveValue instanceof Float f) {
			addFloatField(category, entryBuilder, categoryKey, key, f);
		} else if (primitiveValue instanceof Long l) {
			addLongField(category, entryBuilder, categoryKey, key, l);
		} else if (primitiveValue instanceof Boolean bool) {
			addBooleanField(category, entryBuilder, categoryKey, key, bool);
		} else if (primitiveValue instanceof String string) {
			addStringField(category, entryBuilder, categoryKey, key, string);
		}
	}

	protected void addIntegerField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, int value) {
		IntegerListEntry intEntry = entryBuilder
				.startIntField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(intEntry);
	}

	protected void addLongField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, long value) {
		LongListEntry longEntry = entryBuilder
				.startLongField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(longEntry);
	}

	protected void addFloatField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, float value) {
		FloatListEntry floatEntry = entryBuilder
				.startFloatField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(floatEntry);
	}

	protected void addDoubleField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, double value) {
		DoubleListEntry doubleEntry = entryBuilder
				.startDoubleField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(doubleEntry);
	}

	protected void addBooleanField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, boolean value) {
		BooleanListEntry boolEntry = entryBuilder
				.startBooleanToggle(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(boolEntry);
	}

	protected void addStringField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, String value) {
		StringListEntry stringEntry = entryBuilder
				.startStrField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(stringEntry);
	}

	protected void addColorField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String categoryKey, String key, Color value) {
		ColorEntry colorEntry = entryBuilder
				.startColorField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(TextColor.fromRgb(value.getColor()))
				.setSaveConsumer(newValue -> configFile.set(categoryKey, key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(colorEntry);
	}

}
