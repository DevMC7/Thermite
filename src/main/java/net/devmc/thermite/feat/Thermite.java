package net.devmc.thermite.feat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.devmc.thermite.lib.Mod;
import net.devmc.thermite.lib.config.ConfigFile;
import net.devmc.thermite.lib.config.ConfigScreen;
import net.devmc.thermite.lib.config.screen.ClothConfigScreen;
import net.devmc.thermite.lib.config.types.PrimitiveWrapper;
import net.devmc.thermite.lib.registration.registries.BlockRegistry;
import net.devmc.thermite.lib.registration.registries.ItemRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thermite implements Mod, ModMenuApi {

	public static Thermite INSTANCE;
	private final Logger logger = LoggerFactory.getLogger(getModId());

	@Override
	public void onInitialize() {
		INSTANCE = this;
		ItemRegistry.REGISTRY.init();
		BlockRegistry.REGISTRY.init();
	}

	@Override
	public String getModId() {
		return "thermite";
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		ConfigFile configFile = new ConfigFile(this);
		configFile.setDefault("test", new PrimitiveWrapper(true));
		configFile.load();
		ConfigScreen configScreen = new ClothConfigScreen(configFile);

		return configScreen::createConfigScreen;
	}
}
