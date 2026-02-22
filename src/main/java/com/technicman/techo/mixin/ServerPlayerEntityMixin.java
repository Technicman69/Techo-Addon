package com.technicman.techo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.technicman.techo.power.ReplaceSoundEmissionPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity {

    public ServerPlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void techo$playSound(ServerPlayNetworkHandler instance, Packet<?> packet, Operation<Void> original, SoundEvent event, SoundCategory category, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace(this, event, volume, pitch, (newSound, newVolume, newPitch) -> original.call(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(event), category, this.getX(), this.getY(), this.getZ(), volume, pitch, this.random.nextLong())));
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace(this, sound, volume, pitch, super::playSound);
    }
}
