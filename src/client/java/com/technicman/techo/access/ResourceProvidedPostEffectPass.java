package com.technicman.techo.access;

import com.technicman.techo.util.ResourcePowerValueProvider;

public interface ResourceProvidedPostEffectPass {
    void techo$addProvider(String name, ResourcePowerValueProvider resourceProvider);
}
