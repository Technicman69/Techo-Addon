package com.technicman.techo.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.technicman.techo.util.WeightedSound;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.DataException;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TechoDataTypes {
    public static final SerializableDataType<WeightedSound> WEIGHTED_SOUND = SerializableDataType.compound(
            WeightedSound.class,
            new SerializableData()
                    .add("id", SerializableDataTypes.STRING)
                    .add("pitch", SerializableDataTypes.FLOAT, Float.NaN)
                    .add("volume", SerializableDataTypes.FLOAT, Float.NaN)
                    .add("weight", SerializableDataTypes.INT, 1),
            dataInst -> new WeightedSound(
                    dataInst.get("id"),
                    dataInst.getFloat("pitch"),
                    dataInst.getFloat("volume"),
                    dataInst.getInt("weight")),
            (data, inst) -> {
                SerializableData.Instance dataInst = data.new Instance();
                dataInst.set("id", inst.getId());
                dataInst.set("pitch", inst.getPitch());
                dataInst.set("volume", inst.getVolume());
                dataInst.set("weight", inst.getWeight());
                return dataInst;
            });

    public static final SerializableDataType<List<WeightedSound>> WEIGHTED_SOUNDS = SerializableDataType.list(WEIGHTED_SOUND);

    public static final SerializableDataType<Map<String, List<WeightedSound>>> REGEX_WEIGHTED_SOUNDS_MAP = new SerializableDataType<>(ClassUtil.castClass(Map.class),
            (packetByteBuf, stringMap) -> {
                packetByteBuf.writeInt(stringMap.size());
                stringMap.forEach(((key, value) -> {
                    packetByteBuf.writeString(key);
                    WEIGHTED_SOUNDS.send(packetByteBuf, value);
                }));
            },
            packetByteBuf -> {
                int count = packetByteBuf.readInt();
                Map<String, List<WeightedSound>> map = new LinkedHashMap<>();
                for (int i = 0; i < count; i++) {
                    String s = packetByteBuf.readString();
                    List<WeightedSound> weightedSounds = WEIGHTED_SOUNDS.receive(packetByteBuf);
                    map.put(s, weightedSounds);
                }
                return map;
            }, jsonElement -> {
        if (jsonElement.isJsonObject()) {
            JsonObject jo = jsonElement.getAsJsonObject();
            Map<String, List<WeightedSound>> map = new LinkedHashMap<>();
            for (String s : jo.keySet()) {
                try {
                    JsonElement ele = jo.get(s);
                    JsonPrimitive jp;
                    if (!s.contains(":")) {
                        s = "minecraft:" + s;
                    }
                    if (ele.isJsonPrimitive() && (jp = ele.getAsJsonPrimitive()).isString()) {
                        String id = jp.getAsString();
                        map.put(s, List.of(new WeightedSound(id)));
                    } else if (ele.isJsonObject()) {
                        map.put(s, List.of(WEIGHTED_SOUND.read(ele)));
                    } else if (ele.isJsonArray()) {
                        map.put(s, WEIGHTED_SOUNDS.read(ele));
                    } else {
                        throw new JsonParseException("expected a string, and object or an array");
                    }
                } catch(DataException e) {
                    throw e.prepend('"'+s+'"');
                } catch(Exception e) {
                    throw new DataException(DataException.Phase.READING, s, e);
                }
            }
            return map;
        }
        throw new JsonParseException("Expected a JSON object");
    });
}
