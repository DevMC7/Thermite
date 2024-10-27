package net.devmc.thermite.lib.config.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.devmc.thermite.lib.config.util.JsonSerializable;

public class PrimitiveWrapper implements JsonSerializable {
	private Object value;

	public PrimitiveWrapper() {
	}

	public PrimitiveWrapper(Object value) {
		if (isSupportedType(value)) this.value = value;
		else throw new IllegalArgumentException("Unsupported primitive type: " + value.getClass());
	}

	@Override
	public JsonElement serialize() {
		if (value instanceof Enum<?> enumValue) {
			return new JsonPrimitive(enumValue.ordinal());
		} else if (value instanceof Number || value instanceof Boolean || value instanceof String) {
			return new JsonPrimitive(value.toString());
		} else {
			throw new UnsupportedOperationException("Unsupported value type for serialization: " + value.getClass());
		}
	}

	@Override
	public void deserialize(JsonElement jsonElement, Class<?> targetType) {
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
			if (targetType.isEnum() && primitive.isNumber()) {
				int ordinal = primitive.getAsInt();
				Object[] enumConstants = targetType.getEnumConstants();
				if (ordinal >= 0 && ordinal < enumConstants.length) {
					value = enumConstants[ordinal];
				} else {
					throw new IllegalArgumentException("Invalid ordinal value for enum " + targetType.getSimpleName());
				}
			} else if (primitive.isNumber()) {
				value = primitive.getAsNumber();
			} else if (primitive.isBoolean()) {
				value = primitive.getAsBoolean();
			} else if (primitive.isString()) {
				value = primitive.getAsString();
			} else {
				throw new IllegalArgumentException("Unsupported target type: " + targetType);
			}
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if (isSupportedType(value)) this.value = value;
		else throw new IllegalArgumentException("Unsupported primitive type: " + value.getClass());
	}

	private boolean isSupportedType(Object value) {
		return value instanceof Integer || value instanceof Float || value instanceof Double ||
				value instanceof Boolean || value instanceof String || value instanceof Enum<?>;
	}
}
