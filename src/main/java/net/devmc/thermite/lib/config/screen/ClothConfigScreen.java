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
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Map;

public class ClothConfigScreen extends ConfigScreen {

	private final ConfigFile configFile;

	public ClothConfigScreen(ConfigFile configFile) {
		super(configFile);
		this.configFile = configFile;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Screen createConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable(String.format("title.%s.config", configFile.mod.getModId())));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory generalCategory = builder.getOrCreateCategory(Text.translatable(String.format("category.%s.config", configFile.mod.getModId())));

		for (Map.Entry<String, JsonSerializable> entry : configFile.values.entrySet()) {
			String key = entry.getKey();
			JsonSerializable value = entry.getValue();

			if (value instanceof PrimitiveWrapper primitiveWrapper) {
				Object primitiveValue = primitiveWrapper.getValue();

				if (primitiveValue instanceof Number number) {
					switch (number) {
						case Integer integer -> addIntegerField(generalCategory, entryBuilder, key, integer);
						case Double d -> addDoubleField(generalCategory, entryBuilder, key, d);
						default -> {
							addFloatField(generalCategory, entryBuilder, key, number.floatValue());
						}
					}
				} else if (primitiveValue instanceof Boolean bool) {
					addBooleanField(generalCategory, entryBuilder, key, bool);
				} else if (primitiveValue instanceof String string) {
					addStringField(generalCategory, entryBuilder, key, string);
				}
			} else if (value instanceof ColorWrapper colorWrapper) {
				int color = (int) colorWrapper.getValue();
				addColorField(generalCategory, entryBuilder, key, Color.ofTransparent(color));
			}
		}

		// Save the config when the screen is closed
		builder.setSavingRunnable(configFile::save);

		return builder.build();
	}

	protected void addColorField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, Color value) {
		ColorEntry colorEntry = entryBuilder
				.startColorField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(TextColor.fromRgb(value.getColor()))
				.setSaveConsumer(newValue -> configFile.set(key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(colorEntry);
	}

	protected void addIntegerField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, int value) {
		IntegerListEntry intEntry = entryBuilder
				.startIntField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(intEntry);
	}

	protected void addFloatField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, float value) {
		FloatListEntry floatEntry = entryBuilder
				.startFloatField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(floatEntry);
	}

	protected void addDoubleField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, double value) {
		DoubleListEntry doubleEntry = entryBuilder
				.startDoubleField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(doubleEntry);
	}

	protected void addBooleanField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, boolean value) {
		BooleanListEntry boolEntry = entryBuilder
				.startBooleanToggle(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(boolEntry);
	}

	protected void addStringField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, String value) {
		StringListEntry stringEntry = entryBuilder
				.startStrField(Text.translatable(String.format("option.%s.config.%s", configFile.mod.getModId(), key)), value)
				.setDefaultValue(value)
				.setSaveConsumer(newValue -> configFile.set(key, new PrimitiveWrapper(newValue)))
				.build();
		category.addEntry(stringEntry);
	}
}
