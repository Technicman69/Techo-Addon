package com.technicman.techo.action.entity;

import com.technicman.techo.Techo;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class PlaySoundAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        SoundEvent sound = SoundEvent.of(data.get("sound"));
        SoundCategory category = data.isPresent("category") ? data.get("category") : entity.getSoundCategory();
        if (data.get("follow")) {
            PlayerEntity maybePlayer = !(entity.getWorld() instanceof ServerWorld) && entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
            entity.getWorld().playSoundFromEntity(maybePlayer, entity, sound, category, data.getFloat("volume"), data.getFloat("pitch"));
        } else {
            entity.getWorld().playSound(null, entity.getBlockPos(), sound, category, data.getFloat("volume"), data.getFloat("pitch"));
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                Techo.identifier("play_sound"),
                new SerializableData()
                        .add("sound", SerializableDataTypes.IDENTIFIER)
                        .add("category", SerializableDataType.enumValue(SoundCategory.class), null)
                        .add("volume", SerializableDataTypes.FLOAT, 1.0f)
                        .add("pitch", SerializableDataTypes.FLOAT, 1.0f)
                        .add("follow", SerializableDataTypes.BOOLEAN, false),
                PlaySoundAction::action
        );
    }
}
