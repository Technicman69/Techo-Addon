package com.technicman.techo.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.technicman.techo.power.ReplaceSoundEmissionPower;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends Entity {

    public ClientPlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "playSound(Lnet/minecraft/sound/SoundEvent;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    protected void techo$playSound(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace(this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, x, y, z, newSound, category, newVolume, newPitch, useDistance));
    }

    @WrapOperation(method = "playSound(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    protected void techo$playSound2(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace(this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, x, y, z, newSound, category, newVolume, newPitch, useDistance));
    }
}
