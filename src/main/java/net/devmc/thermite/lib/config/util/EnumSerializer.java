package net.devmc.thermite.lib.config.util;

import com.google.gson.*;

public class EnumSerializer implements JsonSerializer<Enum<?>>, JsonDeserializer<Enum<?>> {

	@Override
	public JsonElement serialize(Enum<?> src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.name());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enum<?> deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			String name = json.getAsString();
			Class<? extends Enum> enumClass = (Class<? extends Enum<?>>) typeOfT;
			try {
				return Enum.valueOf((Class<? extends Enum>) enumClass, name);
			} catch (IllegalArgumentException e) {
				throw new JsonParseException("Invalid enum name: " + name);
			}
		}
		throw new JsonParseException("Invalid enum value: " + json);
	}

}
