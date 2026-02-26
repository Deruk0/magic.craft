package com.example.mixin;

import com.example.core.progression.ProgressionData;
import com.example.net.packet.ProgressionPackets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ProgressionData {
    @Unique
    private int customExp = 0;
    @Unique
    private int customLevel = 1;
    @Unique
    private int statPoints = 0;
    @Unique
    private int strengthLevel = 0;
    @Unique
    private int speedLevel = 0;
    @Unique
    private int healthLevel = 0;
    @Unique
    private int luckLevel = 0;
    @Unique
    private int miningSpeedLevel = 0;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeProgressionDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound modData = new NbtCompound();
        modData.putInt("CustomExp", this.customExp);
        modData.putInt("CustomLevel", this.customLevel);
        modData.putInt("StatPoints", this.statPoints);
        modData.putInt("StrengthLevel", this.strengthLevel);
        modData.putInt("SpeedLevel", this.speedLevel);
        modData.putInt("HealthLevel", this.healthLevel);
        modData.putInt("LuckLevel", this.luckLevel);
        modData.putInt("MiningSpeedLevel", this.miningSpeedLevel);
        nbt.put("TemplateModProgression", modData);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readProgressionDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("TemplateModProgression")) {
            NbtCompound modData = nbt.getCompound("TemplateModProgression");
            this.customExp = modData.getInt("CustomExp");
            this.customLevel = modData.getInt("CustomLevel");
            if (this.customLevel == 0)
                this.customLevel = 1;
            this.statPoints = modData.getInt("StatPoints");
            this.strengthLevel = modData.getInt("StrengthLevel");
            this.speedLevel = modData.getInt("SpeedLevel");
            this.healthLevel = modData.getInt("HealthLevel");
            this.luckLevel = modData.getInt("LuckLevel");
            this.miningSpeedLevel = modData.getInt("MiningSpeedLevel");
            updateAttributes();
        }
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void applyCustomMiningSpeed(net.minecraft.block.BlockState block, CallbackInfoReturnable<Float> cir) {
        if (this.miningSpeedLevel > 0) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * (1.0f + (this.miningSpeedLevel * 0.2f))); // +20% per level
        }
    }

    @Unique
    @SuppressWarnings("resource")
    private void updateAttributes() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient)
            return;

        updateAttribute(player, net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "fcb3d262-6902-4fc8-9993-9c8e2bdceab7", "Strength_Boost", this.strengthLevel * 1.0);
        updateAttribute(player, net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "0a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d", "Speed_Boost", this.speedLevel * 0.015);
        updateAttribute(player, net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH,
                "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", "Health_Boost", this.healthLevel * 2.0); // +1 heart per level
        updateAttribute(player, net.minecraft.entity.attribute.EntityAttributes.GENERIC_LUCK,
                "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e", "Luck_Boost", this.luckLevel * 1.0);
    }

    @Unique
    private void updateAttribute(PlayerEntity player, net.minecraft.entity.attribute.EntityAttribute attribute,
            String uuidStr, String name, double value) {
        net.minecraft.entity.attribute.EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance != null) {
            java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
            instance.removeModifier(uuid);
            if (value > 0) {
                instance.addPersistentModifier(new net.minecraft.entity.attribute.EntityAttributeModifier(
                        uuid, name, value, net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public int getCustomExp() {
        return customExp;
    }

    @Override
    public void setCustomExp(int exp) {
        this.customExp = exp;
        syncProgressionData();
    }

    @Override
    public void addCustomExp(int exp) {
        this.customExp += exp;
        int required = getRequiredExpForNextLevel();
        while (this.customExp >= required) {
            this.customExp -= required;
            this.customLevel++;
            this.statPoints++;
            required = getRequiredExpForNextLevel();
        }
        syncProgressionData();
    }

    @Unique
    private int getRequiredExpForNextLevel() {
        return this.customLevel * 10;
    }

    @Override
    public int getCustomLevel() {
        return customLevel;
    }

    @Override
    public void setCustomLevel(int level) {
        this.customLevel = level;
        syncProgressionData();
    }

    @Override
    public int getStatPoints() {
        return statPoints;
    }

    @Override
    public void setStatPoints(int points) {
        this.statPoints = points;
        syncProgressionData();
    }

    @Override
    public int getStrengthLevel() {
        return strengthLevel;
    }

    @Override
    public void setStrengthLevel(int level) {
        this.strengthLevel = level;
        updateAttributes();
        syncProgressionData();
    }

    @Override
    public int getSpeedLevel() {
        return speedLevel;
    }

    @Override
    public void setSpeedLevel(int level) {
        this.speedLevel = level;
        updateAttributes();
        syncProgressionData();
    }

    @Override
    public int getHealthLevel() {
        return healthLevel;
    }

    @Override
    public void setHealthLevel(int level) {
        this.healthLevel = level;
        updateAttributes();
        syncProgressionData();
    }

    @Override
    public int getLuckLevel() {
        return luckLevel;
    }

    @Override
    public void setLuckLevel(int level) {
        this.luckLevel = level;
        updateAttributes();
        syncProgressionData();
    }

    @Override
    public int getMiningSpeedLevel() {
        return miningSpeedLevel;
    }

    @Override
    public void setMiningSpeedLevel(int level) {
        this.miningSpeedLevel = level;
        syncProgressionData();
    }

    @Override
    public void resetProgression() {
        this.customExp = 0;
        this.customLevel = 1;
        this.statPoints = 0;
        this.strengthLevel = 0;
        this.speedLevel = 0;
        this.healthLevel = 0;
        this.luckLevel = 0;
        this.miningSpeedLevel = 0;
        updateAttributes();
        syncProgressionData();
    }

    @Override
    public void syncProgressionData() {
        if ((Object) this instanceof ServerPlayerEntity serverPlayer) {
            ProgressionPackets.sendSyncPacket(serverPlayer);
        }
    }
}
