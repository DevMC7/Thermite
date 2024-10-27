package net.devmc.thermite;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.devmc.thermite.lib.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thermite implements Mod, ModMenuApi {

	private final Logger logger = LoggerFactory.getLogger(getModId());

	@Override
	public void onInitialize() {

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
