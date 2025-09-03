package com.hbm.render.extended_blockstate;

import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyRandomVariant implements IUnlistedProperty<Integer> {
    public final int VARIANT_COUNT;
    public PropertyRandomVariant(int variantCount) {
        VARIANT_COUNT = variantCount;
    }

    @Override
    public String getName() {
        return "variant";
    }


    @Override
    public boolean isValid(Integer value) {
        return value >= 0 && value < VARIANT_COUNT;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String valueToString(Integer value) {
        return value.toString();
    }
}
