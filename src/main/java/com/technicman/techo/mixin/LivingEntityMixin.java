package com.technicman.techo.mixin;

import com.technicman.techo.power.ReplaceSoundEmissionPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {

    @Override
    protected void techo$playSound(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, except, x, y, z, sound, category, volume, pitch);
    }

    @Redirect(method = "onEquipStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    protected void techo$onEquipStack(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, except, x, y, z, sound, category, volume, pitch);
    }

    @Redirect(method = "playEquipmentBreakEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    protected void techo$playEquipmentBreakEffects(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, (Entity)(Object)this instanceof PlayerEntity ? (PlayerEntity)(Object) this : null, x, y, z, sound, category, volume, pitch);
    }

    @Redirect(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    protected void techo$eatFood(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace((Entity)(Object)this, except, x, y, z, sound, category, volume, pitch);
    }
}
