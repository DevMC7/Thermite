package net.devmc.thermite.feat;

import net.devmc.thermite.lib.Mod;
import net.devmc.thermite.lib.registration.registries.BlockEntityRegistry;
import net.devmc.thermite.lib.registration.registries.BlockRegistry;
import net.devmc.thermite.lib.registration.registries.ItemRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thermite implements Mod {

	public static Thermite INSTANCE;
	private final Logger logger = LoggerFactory.getLogger(getModId());

	@Override
	public void onInitialize() {
		INSTANCE = this;
		ItemRegistry.REGISTRY.init();
		BlockRegistry.REGISTRY.init();
		BlockEntityRegistry.REGISTRY.init();
	}

	@Override
	public String getModId() {
		return "thermite";
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
