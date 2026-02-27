package com.example.entity;

import com.example.TemplateMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntityTypes {

    public static final EntityType<FireballProjectileEntity> FIREBALL_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(TemplateMod.MOD_ID, "fireball_projectile"),
            FabricEntityTypeBuilder.<FireballProjectileEntity>create(SpawnGroup.MISC,
                    FireballProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f, 0.3f))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(5)
                    .build());

    public static void register() {
        TemplateMod.LOGGER.info("Registering Mod Entity Types for " + TemplateMod.MOD_ID);
    }
}
