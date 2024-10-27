package net.devmc.thermite.lib.config;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;
import net.minecraft.client.gui.screen.Screen;

public abstract class ConfigScreen {

	private final ConfigFile configFile;

	public ConfigScreen(ConfigFile configFile) {
		this.configFile = configFile;
	}

	abstract public Screen createConfigScreen(Screen parent);

	protected abstract void addEnumField(ConfigCategory generalCategory, ConfigEntryBuilder entryBuilder, String key, Enum<?> enumValue, Class<Enum<?>> classType);
	protected abstract void addColorField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, Color value);
	protected abstract void addIntegerField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, int value);
	protected abstract void addFloatField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, float value);
	protected abstract void addDoubleField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, double value);
	protected abstract void addBooleanField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, boolean value);
	protected abstract void addStringField(ConfigCategory category, ConfigEntryBuilder entryBuilder, String key, String value);

}
