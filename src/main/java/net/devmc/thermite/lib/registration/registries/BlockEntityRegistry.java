package net.devmc.thermite.lib.registration.registries;

import net.devmc.thermite.lib.registration.annotations.NoRegistration;
import net.devmc.thermite.lib.registration.registers.BlockRegister;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class BlockEntityRegistry implements net.devmc.thermite.lib.registration.registries.Registry<BlockRegister> {

	public static final BlockEntityRegistry REGISTRY = new BlockEntityRegistry();
	private final List<BlockRegister> classes = new ArrayList<>();

	@Override
	public void register(BlockRegister register) {
		classes.add(register);
	}

	@Override
	public void registerAll(List<BlockRegister> registers) {
		classes.addAll(registers);
	}

	@Override
	public void registerAll(BlockRegister[] registers) {
		classes.addAll(List.of(registers));
	}

	@ApiStatus.Internal
	public void init() {
		for (BlockRegister register : classes) {
			for (Field field : register.getClass().getFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(NoRegistration.class)) continue;
				try {
					if (BlockEntityType.class.isAssignableFrom(field.getType())) {
						BlockEntityType<?> blockEntityType = (BlockEntityType<?>) field.get(register);
						Identifier id = Identifier.ofVanilla(field.getName());
						Registry.register(Registries.BLOCK_ENTITY_TYPE, id, blockEntityType);
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Failed to access field " + field.getName(), e);
				}
			}
		}
	}
}
