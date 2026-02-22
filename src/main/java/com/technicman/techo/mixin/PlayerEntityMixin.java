package com.technicman.techo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.technicman.techo.power.ReplaceSoundEmissionPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void techo$attack(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace(this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, except, x, y, z, newSound, category, newVolume, newPitch));
    }

    @WrapOperation(method = "addExperienceLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    protected void techo$addExperienceLevels(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace(this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, except, x, y, z, newSound, category, newVolume, newPitch));
    }

    @WrapOperation(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    protected void techo$eatFood(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        ReplaceSoundEmissionPower.tryReplace(this, sound, volume, pitch, (newSound, newVolume, newPitch) -> original.call(instance, except, x, y, z, newSound, category, newVolume, newPitch));
    }
}
