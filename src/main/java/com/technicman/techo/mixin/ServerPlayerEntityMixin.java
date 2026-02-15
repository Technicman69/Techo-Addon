package com.technicman.techo.mixin;

import com.technicman.techo.power.ReplaceSoundEmissionPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity {

    public ServerPlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void techo$playSound(ServerPlayNetworkHandler serverPlayNetworkHandler, Packet<?> packet, SoundEvent event, SoundCategory category, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace(this, null, this.getX(), this.getY(), this.getZ(), event, category, volume, pitch);
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        ReplaceSoundEmissionPower.tryReplace(this, (ServerPlayerEntity) (Object) this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
    }
}
