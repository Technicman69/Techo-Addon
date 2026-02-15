package com.technicman.techo.power;

import com.technicman.techo.Techo;
import com.technicman.techo.util.Weighted;
import com.technicman.techo.util.WeightedSound;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.technicman.techo.data.TechoDataTypes.REGEX_WEIGHTED_SOUNDS_MAP;

public class ReplaceSoundEmissionPower extends Power {
    public static Random random = Random.create();

    private final Map<String, List<WeightedSound>> replacements;
    private final Map<String, Boolean> captureGroup;

    private final boolean replace;
    private final Consumer<Entity> entityAction;
    private final int priority;

    public ReplaceSoundEmissionPower(PowerType<?> type, LivingEntity entity, Map<String, List<WeightedSound>> replacements, boolean replace, Consumer<Entity> entityAction, int priority) {
        super(type, entity);
        this.replacements = replacements;
        this.replace = replace;
        this.entityAction = entityAction;
        this.priority = priority;

        captureGroup = new HashMap<>(replacements.size());
        for (String key : replacements.keySet()) {
            boolean contains$ = false;
            for (WeightedSound sound : replacements.get(key)) {
                if (sound.getId().contains("$")) {
                    contains$ = true;
                    break;
                }
            }
            captureGroup.put(key, contains$);
        }
    }

    public boolean doReplace() {
        return replace;
    }

    public WeightedSound pickRandomFromKey(String key) {
        List<WeightedSound> list = replacements.get(key);
        if (list.size() == 1) {
            return list.get(0);
        }
        return (WeightedSound) Weighted.randomChoice(list, random.nextInt());
    }

    public WeightedSound getReplacement(SoundEvent sound) {
        String name = sound.getId().toString();
        //System.out.println("Searching for replacement... " + name);
        if(replacements.containsKey(name)) {
            //System.out.println("Simple key found");
            return pickRandomFromKey(name);
        }
        Set<String> keys = replacements.keySet();
        for(String s : keys) {
            //System.out.println(s +" ?");
            if(!captureGroup.get(s)) {
                if(name.matches(s)) {
                    //System.out.println("No capturing key found");
                    return pickRandomFromKey(s);
                }
            } else {
                Pattern pattern = Pattern.compile(s);
                Matcher matcher = pattern.matcher(name);
                if(matcher.matches()) {
//                    System.out.println("Capturing key found");
//                    System.out.println(s + " matches " + name + "!");
                    WeightedSound weightedSound = pickRandomFromKey(s);
                    String newId = matcher.replaceAll(weightedSound.getId());
                    return new WeightedSound(newId, weightedSound.getVolume(), weightedSound.getPitch(), 1);
                }
            }
        }
        return null;
    }

    public static void tryReplace(Entity sourceEntity, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        List<ReplaceSoundEmissionPower> replaceSoundEmissionPowers = PowerHolderComponent.getPowers(sourceEntity, ReplaceSoundEmissionPower.class)
                .stream()
                .sorted(Comparator.comparing(ReplaceSoundEmissionPower::getPriority)
                        .reversed()
                        .thenComparing(ReplaceSoundEmissionPower::doReplace))
                .toList();
        for (ReplaceSoundEmissionPower power : replaceSoundEmissionPowers) {
            //System.out.println("Try replace for power " + power.toString());
            WeightedSound replacement = power.getReplacement(sound);
            if (replacement != null) {
                if (power.entityAction != null) {
                    power.entityAction.accept(sourceEntity);
                }
//                System.out.println("Replaced " + sound.getId() + " sound with " + replacement.getId());
//                System.out.println("Volume: " + volume + ", Pitch: " + pitch);
                if (!Objects.equals(replacement.getId(), "minecraft:empty")) {
                    SoundEvent newSound = SoundEvent.of(Identifier.tryParse(replacement.getId()));
                    float newVolume = Float.isNaN(replacement.getVolume()) ? volume : replacement.getVolume();
                    float newPitch = Float.isNaN(replacement.getPitch()) ? pitch : replacement.getPitch();
                    playSound(sourceEntity, except, x, y, z, newSound, category, newVolume, newPitch);
                }
                if (power.replace) {
                    return;
                }
            }
        }
        playSound(sourceEntity, except, x, y, z, sound, category, volume, pitch);
    }

    public static void playSound(Entity sourceEntity, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (sourceEntity instanceof ServerPlayerEntity) {
            ServerPlayNetworkHandler networkHandler = ((ServerPlayerEntity) sourceEntity).networkHandler;
            if (networkHandler != null) {
                networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(sound), category, x, y, z, volume, pitch, random.nextLong()));
            }
        } else {
            sourceEntity.getWorld().playSound(except, x, y, z, sound, category, volume, pitch);
        }
    }

    public int getPriority() {
        return priority;
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(
                Techo.identifier("replace_sound_emission"),
                new SerializableData()
                        .add("sounds", REGEX_WEIGHTED_SOUNDS_MAP)
                        .add("replace", SerializableDataTypes.BOOLEAN, true)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("priority", SerializableDataTypes.INT, 0),
                    data -> (type, entity) -> new ReplaceSoundEmissionPower(type, entity,
                        data.get("sounds"),
                        data.get("replace"),
                        data.get("entity_action"),
                        data.get("priority"))
        ).allowCondition();
    }
}
