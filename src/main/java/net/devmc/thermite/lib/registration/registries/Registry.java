package net.devmc.thermite.lib.registration.registries;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface Registry {
	void register(Class<?> clazz);
	void registerAll(List<Class<?>> registers);
	void registerAll(Class<?>... registers);

	@ApiStatus.Internal
	void init();
}
