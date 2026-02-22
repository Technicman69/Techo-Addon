package com.technicman.techo.mixin.client;

import com.technicman.techo.access.ResourceProvidedPostEffectPass;
import com.technicman.techo.util.ResourcePowerValueProvider;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(PostEffectPass.class)
public abstract class PostEffectPassMixin implements ResourceProvidedPostEffectPass {

    @Shadow public abstract JsonEffectShaderProgram getProgram();

    @Unique
    private final Map<String, List<ResourcePowerValueProvider>> providers = new HashMap<>(0);

    @Override
    public void techo$addProvider(String name, ResourcePowerValueProvider resourceProvider) {
        if (providers.containsKey(name)) {
            providers.get(name).add(resourceProvider);
        } else {
            providers.put(name, new ArrayList<>(List.of(resourceProvider)));
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/JsonEffectShaderProgram;enable()V"))
    public void updateUniforms(float time, CallbackInfo ci) {
        for (String key : providers.keySet()) {
            GlUniform uniform = getProgram().getUniformByName(key);
            if (uniform != null) {
                float[] values = new float[uniform.getCount()];
                FloatBuffer buffer = uniform.getFloatData();
                for (int i=0; i<values.length; i++) {
                    values[i] = buffer.get(i);
                }
                for (ResourcePowerValueProvider resourceProvider : providers.get(key)) {
                    values[resourceProvider.getIndex()] = resourceProvider.getValue();
                }
                if (values.length == 4) {
                    uniform.setAndFlip(values[0], values[1], values[2], values[3]);
                } else {
                    uniform.set(values);
                }
            }
        }
    }
}
