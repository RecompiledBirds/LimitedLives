package com.onelifemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class CoreUtils {
    public static final String persistName = "PlayerPersisted";
    public static final String persistTagName = "DeathData";
    public static CompoundTag GetPersistentTag(LivingEntity e) {
        CompoundTag persistData = e.getPersistentData().getCompound(persistName);
        e.getPersistentData().put("PlayerPersisted", persistData);
        CompoundTag tag = persistData.getCompound(persistTagName);
        persistData.put(persistTagName, tag);
        return tag;
    }
}
