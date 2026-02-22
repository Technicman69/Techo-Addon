package com.technicman.techo.mixin.client;

import com.technicman.techo.access.ReplaceableSoundInstance;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin implements ReplaceableSoundInstance {

    @Mutable
    @Shadow @Final protected Identifier id;

    @Shadow protected float volume;

    @Shadow protected float pitch;

    @Override
    public ReplaceableSoundInstance techo$replaceWith(SoundEvent sound, float volume, float pitch) {
        this.id = sound.getId();
        this.volume = volume;
        this.pitch = pitch;
        return this;
    }

    @Override
    public float techo$getBaseVolume() {
        return volume;
    }

    @Override
    public float techo$getBasePitch() {
        return pitch;
    }
}
