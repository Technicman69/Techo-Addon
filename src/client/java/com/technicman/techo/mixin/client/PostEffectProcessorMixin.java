package com.technicman.techo.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.technicman.techo.access.ResourceProvidedPostEffectPass;
import com.technicman.techo.util.ResourcePowerValueProvider;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PostEffectProcessor.class)
public class PostEffectProcessorMixin {

    @Shadow @Final private List<PostEffectPass> passes;

    @WrapOperation(method = "parseUniform", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;asFloat(Lcom/google/gson/JsonElement;Ljava/lang/String;)F"))
    private float parseResourceProvider(JsonElement element, String valueFieldName, Operation<Float> original, JsonElement jsonElement, @Local String name, @Local GlUniform glUniform, @Local int i) {
        ResourcePowerValueProvider resourceProvider = null;
        if (element instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {
            resourceProvider = new ResourcePowerValueProvider(i, jsonPrimitive.getAsString());
        }
        if (element instanceof JsonObject object) {
            resourceProvider = ResourcePowerValueProvider.parse(i, object);
        }
        if (resourceProvider != null) {
            ((ResourceProvidedPostEffectPass) this.passes.get(this.passes.size() - 1)).techo$addProvider(name, resourceProvider);
            return resourceProvider.getValue();
        }
        return original.call(element, valueFieldName);
    }
}
