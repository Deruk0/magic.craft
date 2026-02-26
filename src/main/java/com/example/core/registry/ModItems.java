package com.example.core.registry;

import com.example.TemplateMod;
import com.example.item.FireStaffItem;
import com.example.item.IceStaffItem;
import com.example.item.LightningStaffItem;
import com.example.item.NatureStaffItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item FIRE_STAFF = registerItem("fire_staff",
            new FireStaffItem(new FabricItemSettings().maxCount(1)));
    public static final Item ICE_STAFF = registerItem("ice_staff",
            new IceStaffItem(new FabricItemSettings().maxCount(1)));
    public static final Item LIGHTNING_STAFF = registerItem("lightning_staff",
            new LightningStaffItem(new FabricItemSettings().maxCount(1)));
    public static final Item NATURE_STAFF = registerItem("nature_staff",
            new NatureStaffItem(new FabricItemSettings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(TemplateMod.MOD_ID, name), item);
    }

    private static void addItemsToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.add(FIRE_STAFF);
        entries.add(ICE_STAFF);
        entries.add(LIGHTNING_STAFF);
        entries.add(NATURE_STAFF);
    }

    public static void register() {
        TemplateMod.LOGGER.info("Registering Mod Items for " + TemplateMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatItemGroup);
    }
}
