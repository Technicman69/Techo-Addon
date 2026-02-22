package com.technicman.techo.power;

import com.technicman.techo.Techo;
import com.technicman.techo.util.WeightedSound;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.technicman.techo.data.TechoDataTypes.REGEX_WEIGHTED_SOUNDS_MAP;

public class ReplaceSoundReceptionPower extends ReplaceSoundPower {


    public ReplaceSoundReceptionPower(PowerType<?> type, LivingEntity entity, Map<String, List<WeightedSound>> replacements, boolean replace, Consumer<Entity> entityAction, int priority) {
        super(type, entity, replacements, replace, entityAction, priority);
    }

    public static void tryReplace(Entity sourceEntity, SoundEvent sound, float volume, float pitch, PlaySoundCallback callback) {
        ReplaceSoundPower.tryReplace(ReplaceSoundReceptionPower.class, sourceEntity, sound, volume, pitch, callback);
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(
                Techo.identifier("replace_sound_reception"),
                new SerializableData()
                        .add("sounds", REGEX_WEIGHTED_SOUNDS_MAP)
                        .add("replace", SerializableDataTypes.BOOLEAN, true)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("priority", SerializableDataTypes.INT, 0),
                    data -> (type, entity) -> new ReplaceSoundReceptionPower(type, entity,
                        data.get("sounds"),
                        data.get("replace"),
                        data.get("entity_action"),
                        data.get("priority"))
        ).allowCondition();
    }
}
