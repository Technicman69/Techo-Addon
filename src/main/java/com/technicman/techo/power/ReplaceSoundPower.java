package com.technicman.techo.power;

import com.technicman.techo.util.Weighted;
import com.technicman.techo.util.WeightedSound;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ReplaceSoundPower extends Power {
    public static Random random = Random.create();

    private final Map<String, List<WeightedSound>> replacements;
    private final Map<String, Boolean> captureGroup;

    final boolean replace;
    final Consumer<Entity> entityAction;
    private final int priority;

    public ReplaceSoundPower(PowerType<?> type, LivingEntity entity, Map<String, List<WeightedSound>> replacements, boolean replace, Consumer<Entity> entityAction, int priority) {
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

    @FunctionalInterface
    public interface PlaySoundCallback {
        void playSound(SoundEvent sound, float volume, float pitch);
    }

    /** Tries to replace provided sound and/or volume and/or pitch using rules defined in given subclass of ReplaceSoundPower that the provided source entity has.
     * <p>
     * If the sound is replaced by `minecraft:empty` then `callback` won't be called.
     *
     * @param powerClass class of the Replace Sound Power subtype to be used
     * @param sourceEntity the living entity whose powers should try to replace the sound
     * @param sound original sound that should be tried to be replaced
     * @param volume original volume of the sound
     * @param pitch original pitch of the sound
     * @param callback the function that was supposed to play the original sound. Won't be called if sound is replaced by `minecraft:empty`
     * @param <T> type of the Replace Sound Power subtype
     */
    public static <T extends ReplaceSoundPower> void tryReplace(Class<T> powerClass, Entity sourceEntity, SoundEvent sound, float volume, float pitch, PlaySoundCallback callback) {
        List<T> replaceSoundEmissionPowers = PowerHolderComponent.getPowers(sourceEntity, powerClass)
                .stream()
                .sorted(Comparator.comparing(ReplaceSoundPower::getPriority)
                        .reversed()
                        .thenComparing(ReplaceSoundPower::doReplace))
                .toList();
        for (T power : replaceSoundEmissionPowers) {
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
                    callback.playSound(newSound, newVolume, newPitch);
                }
                if (power.replace) {
                    return;
                }
            }
        }
        callback.playSound(sound, volume, pitch);
    }

    public int getPriority() {
        return priority;
    }
}
