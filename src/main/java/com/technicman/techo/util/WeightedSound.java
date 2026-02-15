package com.technicman.techo.util;

public final class WeightedSound implements Weighted {
    private final String id;
    private final float volume;
    private final float pitch;
    private final int weight;

    public WeightedSound(String id, float volume, float pitch, int weight) {
        Weighted.validateWeight(weight);
        if (pitch <= 0) throw new IllegalArgumentException("Expected pitch to be greater than 0 (current value: " + pitch + ")");
        if (volume <= 0) throw new IllegalArgumentException("Expected volume to be greater than 0 (current value: " + volume + ")");
        this.id = id;
        this.volume = volume;
        this.pitch = pitch;
        this.weight = weight;
    }

    public WeightedSound(String id, float volume, float pitch) {
        this(id, volume, pitch, 1);
    }

    public WeightedSound(String id) {
        this(id, Float.NaN, Float.NaN);
    }

    public String getId() {
        return id;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "WeightedSound{" +
                "id='" + id + '\'' +
                ", volume=" + volume +
                ", pitch=" + pitch +
                ", weight=" + weight +
                '}';
    }
}
