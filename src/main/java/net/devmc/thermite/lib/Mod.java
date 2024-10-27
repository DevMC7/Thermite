package net.devmc.thermite.lib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public interface Mod extends ModInitializer {

	String getModId();
	Logger getLogger();
	//Optional<Class<Client>> getClient();
}
