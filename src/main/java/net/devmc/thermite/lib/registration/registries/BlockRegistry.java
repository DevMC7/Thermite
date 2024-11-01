package net.devmc.thermite.lib.registration.registries;

import net.devmc.thermite.lib.registration.annotations.NoBlockItem;
import net.devmc.thermite.lib.registration.annotations.NoRegistration;
import net.devmc.thermite.lib.registration.registers.BlockRegister;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class BlockRegistry implements net.devmc.thermite.lib.registration.registries.Registry<BlockRegister> {

	public static final BlockRegistry REGISTRY = new BlockRegistry();
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
	public void registerAll(BlockRegister... registers) {
		classes.addAll(List.of(registers));
	}

	@ApiStatus.Internal
	public void init() {
		for (BlockRegister register : classes) {
			for (Field field : register.getClass().getFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(NoRegistration.class)) continue;
				try {
					if (Block.class.isAssignableFrom(field.getType())) {
						Block block = (Block) field.get(register);
						Identifier id = Identifier.ofVanilla(field.getName());
						Registry.register(Registries.BLOCK, id, block);

						if (!field.isAnnotationPresent(NoBlockItem.class)) {
							Item item = new BlockItem(block, new Item.Settings());
							Registry.register(Registries.ITEM, id, item);
						}
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Failed to access field " + field.getName(), e);
				}
			}
		}
	}
}
