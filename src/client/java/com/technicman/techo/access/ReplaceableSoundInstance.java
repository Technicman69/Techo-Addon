package com.technicman.techo.access;

import net.minecraft.sound.SoundEvent;

public interface ReplaceableSoundInstance {
    ReplaceableSoundInstance techo$replaceWith(SoundEvent sound, float volume, float pitch);
    float techo$getBaseVolume();
    float techo$getBasePitch();
}
