package net.devmc.thermite.lib.config.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.devmc.thermite.lib.config.util.JsonSerializableRegistry;
import net.devmc.thermite.lib.config.util.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

public class ArrayWrapper<T extends JsonSerializable> implements JsonSerializable {

	private final List<T> elements = new ArrayList<>();

	public ArrayWrapper() {
	}

	@Override
	public JsonElement serialize() {
		JsonArray jsonArray = new JsonArray();
		for (T element : elements) {
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
				JsonSerializable value = JsonSerializableRegistry.createFromClass(clazz.asSubclass(JsonSerializable.class), element);
				if (clazz.isInstance(value)) {
					@SuppressWarnings("unchecked")
					T typedValue = (T) value;
					elements.add(typedValue);
				} else {
					throw new IllegalArgumentException("Element is not of the expected type: " + clazz.getName());
				}
			}
		}
	}

	@Override
	public Object getValue() {
		return elements;
	}

	public List<T> getElements() {
		return elements;
	}

	public void addElement(T element) {
		elements.add(element);
	}
}
