package net.devmc.thermite;

import net.devmc.thermite.lib.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thermite implements Mod {

	public static Thermite INSTANCE;
	private final Logger logger = LoggerFactory.getLogger(getModId());

	@Override
	public void onInitialize() {
		INSTANCE = this;
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
