package net.devmc.thermite.lib.registration.registries;

import net.devmc.thermite.lib.registration.annotations.NoRegistration;
import net.devmc.thermite.lib.registration.registers.ItemRegister;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class ItemRegistry implements net.devmc.thermite.lib.registration.registries.Registry<ItemRegister> {

	public static final ItemRegistry REGISTRY = new ItemRegistry();

	private final List<ItemRegister> classes = new ArrayList<>();

	@Override
	public void register(ItemRegister register) {
		classes.add(register);
	}

	public void registerAll(List<ItemRegister> registers) {
		classes.addAll(registers);
	}

	@Override
	public void registerAll(ItemRegister... registers) {
		classes.addAll(List.of(registers));
	}

	@ApiStatus.Internal
	public void init() {
		for (ItemRegister register : classes) {
			for (Field field : register.getClass().getFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(NoRegistration.class)) continue;
				if (Item.class.isAssignableFrom(field.getType())) {
					try {
                        Item item = (Item) field.get(register);
						Registry.register(Registries.ITEM, Identifier.ofVanilla(field.getName()), item);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to access field " + field.getName(), e);
                    }
				}
			}
		}
	}

}
