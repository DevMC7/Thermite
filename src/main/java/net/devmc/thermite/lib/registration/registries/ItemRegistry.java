package net.devmc.thermite.lib.registration.registries;

import net.devmc.thermite.lib.registration.annotations.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemRegistry implements net.devmc.thermite.lib.registration.registries.Registry {

	public static final ItemRegistry REGISTRY = new ItemRegistry();
	private final List<Class<?>> classes = new ArrayList<>();

	@Override
	public void register(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Register.class) &&
				clazz.getAnnotation(Register.class).value().equals(Item.class)) {
			classes.add(clazz);
		}
	}

	@Override
	public void registerAll(List<Class<?>> registers) {
		registers.forEach(clazz -> {
			if (clazz.isAnnotationPresent(Register.class) && clazz.getAnnotation(Register.class).value().equals(Item.class)) {
				classes.add(clazz);
			}
		});
	}

	@Override
	public void registerAll(Class<?>... registers) {
		for (Class<?> clazz : registers) {
			if (clazz.isAnnotationPresent(Register.class) && clazz.getAnnotation(Register.class).value().equals(Item.class)) {
				classes.add(clazz);
			}
		}
	}

	@Override
	@ApiStatus.Internal
	public void init() {
		for (Class<?> clazz : classes) {
			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(NoRegistration.class)) continue;
				if (Item.class.isAssignableFrom(field.getType())) {
					try {
						Item item = (Item) field.get(clazz);
						String id = field.isAnnotationPresent(ModId.class) ? field.getAnnotation(ModId.class).modid()
								.toLowerCase()
								.replaceAll(" ", "_")
								: "minecraft";
						String name = field.isAnnotationPresent(Name.class) ? field.getAnnotation(Name.class).name()
								.toLowerCase()
								.replaceAll(" ", "_")
								: field.getName();
						Registry.register(Registries.ITEM, Identifier.of(id, name), item);
					} catch (IllegalAccessException e) {
						throw new RuntimeException("Failed to access field " + field.getName(), e);
					}
				}
			}
		}
	}
}

