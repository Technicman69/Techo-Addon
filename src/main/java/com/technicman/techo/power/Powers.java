package com.technicman.techo.power;

import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.power.factory.PowerFactory;
import net.minecraft.registry.Registry;

public class Powers {
    public static void register() {
        register(ReplaceSoundEmissionPower.createFactory());
        register(ReplaceSoundReceptionPower.createFactory());
        register(ScreenShakePower.createFactory());
    }

    private static void register(PowerFactory<?> powerFactory) {
        Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
    }
}
