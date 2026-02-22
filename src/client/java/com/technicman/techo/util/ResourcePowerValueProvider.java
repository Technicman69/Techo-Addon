package com.technicman.techo.util;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.VariableIntPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.security.InvalidParameterException;

public class ResourcePowerValueProvider {
    private final int index;

    private final VariableIntPower resource;
    private final float scale;
    private final float offset;

    private static VariableIntPower parseResource(String resource) {
        Identifier id = new Identifier(resource);
        PlayerEntity player = MinecraftClient.getInstance().player;
        Power resourcePower = new PowerTypeReference<>(id).get(player);
        if (resourcePower == null) {
            throw new InvalidParameterException("Player doesn't have the '" + resource + "' power");
        }
        if (!(resourcePower instanceof VariableIntPower)) {
            throw new InvalidParameterException("Expected '" + resource + "' power to be of Resource type");
        }
        return (VariableIntPower) resourcePower;
    }

    public ResourcePowerValueProvider(int index, String resource) {
        this(index, resource, 0.01f);
    }

    public ResourcePowerValueProvider(int index, String resource, float scale) {
        this.index = index;
        this.resource = parseResource(resource);
        this.scale = scale;
        this.offset = 0;
    }

    public ResourcePowerValueProvider(int index, String resource, float min, float max) {
        this.index = index;
        this.resource = parseResource(resource);
        this.scale = (max - min) / (this.resource.getMax() - this.resource.getMin());
        this.offset = min - this.resource.getMin() * scale;
    }

    public static ResourcePowerValueProvider parse(int index, JsonObject object) {
        if (object.has("min") || object.has("max")) {
//            if (object.has("scale")) {
//                throw new InvalidParameterException("Can't define both 'scale' and 'min', 'max' pair");
//            }
            return new ResourcePowerValueProvider(
                    index,
                    JsonHelper.getString(object, "resource"),
                    JsonHelper.getFloat(object, "min"),
                    JsonHelper.getFloat(object, "max")
            );
        } else {
            return new ResourcePowerValueProvider(
                    index,
                    JsonHelper.getString(object, "resource"),
                    JsonHelper.getFloat(object, "scale", 0.01f)
            );
        }
    }

    public float getValue() {
        return resource.getValue() * scale + offset;
    }

    public int getIndex() {
        return index;
    }
}
