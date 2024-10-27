package net.devmc.thermite.lib.config.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.devmc.thermite.lib.config.JsonSerializableRegistry;
import net.devmc.thermite.lib.config.util.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

public class ArrayWrapper implements JsonSerializable {

	private final List<JsonSerializable> elements = new ArrayList<>();

	public ArrayWrapper() {
	}

	@Override
	public JsonElement serialize() {
		JsonArray jsonArray = new JsonArray();
		for (JsonSerializable element : elements) {
			jsonArray.add(element.serialize());
		}
		return jsonArray;
	}

	@Override
	public void deserialize(JsonElement jsonElement, Class<?> clazz) {
		if (jsonElement.isJsonArray()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			elements.clear();
			for (JsonElement element : jsonArray) {
				JsonSerializable value = JsonSerializableRegistry.create("PrimitiveWrapper", element);
				elements.add(value);
			}
		}
	}

	public List<JsonSerializable> getElements() {
		return elements;
	}

	public void addElement(JsonSerializable element) {
		elements.add(element);
	}
}
