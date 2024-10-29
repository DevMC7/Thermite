package net.devmc.thermite.lib.config.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.devmc.thermite.Thermite;
import net.devmc.thermite.lib.config.util.JsonSerializable;

public class ColorWrapper implements JsonSerializable {

	private int color = 0;

	public ColorWrapper() {
		this(255, 255, 255, 255);
    }

	public ColorWrapper(int color) {
		this.color = color;
	}

	public ColorWrapper(int r, int g, int b) {
		this(((0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}

	public ColorWrapper(int r, int g, int b, int a) {
		this.color = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}


	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(color);
	}

	@Override
	public void deserialize(JsonElement jsonElement, Class<?> targetType) {
		try {
			if (jsonElement.isJsonPrimitive()) {
				this.color = jsonElement.getAsJsonPrimitive().getAsInt();
			} else if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				int r = jsonObject.get("r").getAsInt();
				int g = jsonObject.get("g").getAsInt();
				int b = jsonObject.get("b").getAsInt();
				int a = jsonObject.has("a") ? jsonObject.get("a").getAsInt() : 255;
				this.setColor(r, g, b, a);
			}
		} catch (Exception e) {
			this.color = 0xFFFFFFFF;
			Thermite.INSTANCE.getLogger().error("Error deserializing color from JSON: {}", jsonElement, e);
		}
	}

	@Override
	public Object getValue() {
		return this.color;
	}

	public int getRed() {
		return color >> 16 & 0xFF;
	}

	public int getGreen() {
		return color >> 8 & 0xFF;
	}

	public int getBlue() {
		return color & 0xFF;
	}

	public String toHexString() {
		return String.format("#%08X", color);
	}

	public int getAlpha() {
		return (color >> 24) & 0xFF;
	}

	public void setAlpha(int alpha) {
		this.color = (this.color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setColor(int r, int g, int b) {
		this.setColor(r, g, b, 255);
	}

	public void setColor(int r, int g, int b, int a) {
		this.setColor(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}

	public void setColor(String hex) {
		try {
			this.color = Integer.parseUnsignedInt(hex.replace("#", ""), 16);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid hex color format: " + hex);
		}
	}

	public void setColorFromShortHex(String hex) {
		if (hex.length() == 4) {
			int r = Integer.parseInt(hex.substring(1, 2), 16) * 17;
			int g = Integer.parseInt(hex.substring(2, 3), 16) * 17;
			int b = Integer.parseInt(hex.substring(3, 4), 16) * 17;
			setColor(r, g, b);
		} else {
			throw new IllegalArgumentException("Invalid short hex color format: " + hex);
		}
	}

	public ColorWrapper blend(ColorWrapper other, float ratio) {
		int r = (int) (getRed() * (1 - ratio) + other.getRed() * ratio);
		int g = (int) (getGreen() * (1 - ratio) + other.getGreen() * ratio);
		int b = (int) (getBlue() * (1 - ratio) + other.getBlue() * ratio);
		int a = (int) (getAlpha() * (1 - ratio) + other.getAlpha() * ratio);
		return new ColorWrapper(r, g, b, a);
	}

}
