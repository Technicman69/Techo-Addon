package com.technicman.techo.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.technicman.techo.access.ReplaceableSoundInstance;
import com.technicman.techo.power.ReplaceSoundReceptionPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @WrapMethod(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V")
    private void techo$play(SoundInstance sound, Operation<Void> original) {
        if (sound instanceof AbstractSoundInstance) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                ReplaceableSoundInstance s = (ReplaceableSoundInstance) sound;
                ReplaceSoundReceptionPower.tryReplace(player, SoundEvent.of(sound.getId()), s.techo$getBaseVolume(), s.techo$getBasePitch(),
                        (newSound, newVolume, newPitch) -> original.call(s.techo$replaceWith(newSound, newVolume, newPitch)));
                return;
            }
        }
        original.call(sound);
    }
}
