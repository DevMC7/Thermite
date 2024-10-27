package net.devmc.thermite.lib.config;

import net.minecraft.client.gui.screen.Screen;

public abstract class ConfigScreen {

	private final ConfigFile configFile;

	public ConfigScreen(ConfigFile configFile) {
		this.configFile = configFile;
	}

	abstract public Screen createConfigScreen(Screen parent);

}
