package net.devmc.thermite.lib.registration.registries;

import net.devmc.thermite.lib.registration.registers.Register;

import java.util.List;

public interface Registry<T extends Register> {

	void register(T register);
	void registerAll(List<T> registers);
	void registerAll(T... registers);
}
