package com.technicman.techo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.technicman.techo.power.ReplaceSoundEmissionPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {

    @Override
    protected void techo$playSound(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, sound, volume, pitch, (newSound, newVolume, newPitch) -> instance.playSound(except, x, y, z, newSound, category, newVolume, newPitch));
    }

    @WrapOperation(method = "onEquipStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    protected void techo$onEquipStack(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, except, x, y, z, newSound, category, newVolume, newPitch));
    }

    @WrapOperation(method = "playEquipmentBreakEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    protected void techo$playEquipmentBreakEffects(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, x, y, z, newSound, category, newVolume, newPitch, useDistance));
    }

    @WrapOperation(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    protected void techo$eatFood(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, except, x, y, z, newSound, category, newVolume, newPitch));
    }
}
