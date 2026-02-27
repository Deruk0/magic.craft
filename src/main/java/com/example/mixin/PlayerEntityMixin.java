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
    @Unique
    private float currentMana = 50.0f; // Base 50 mana
    @Unique
    private int maxManaLevel = 0;
    @Unique
    private int manaRegenLevel = 0;
    @Unique
    private boolean fireballSkillUnlocked = false;

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
        modData.putFloat("CurrentMana", this.currentMana);
        modData.putInt("MaxManaLevel", this.maxManaLevel);
        modData.putInt("ManaRegenLevel", this.manaRegenLevel);
        modData.putBoolean("FireballSkillUnlocked", this.fireballSkillUnlocked);
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
            if (modData.contains("CurrentMana")) {
                this.currentMana = modData.getFloat("CurrentMana");
                this.maxManaLevel = modData.getInt("MaxManaLevel");
                this.manaRegenLevel = modData.getInt("ManaRegenLevel");
            } else {
                this.currentMana = 50.0f;
            }
            if (modData.contains("FireballSkillUnlocked")) {
                this.fireballSkillUnlocked = modData.getBoolean("FireballSkillUnlocked");
            }
            updateAttributes();
        }
    }

    /**
     * Called by Fabric's ServerPlayerEvents.COPY_FROM (registered in
     * ServerEventsRegistration).
     * Copies all progression fields from the old player instance to this new one on
     * respawn.
     */
    @Override
    public void copyProgressionFrom(ProgressionData oldData) {
        this.customExp = oldData.getCustomExp();
        this.customLevel = oldData.getCustomLevel();
        this.statPoints = oldData.getStatPoints();
        this.strengthLevel = oldData.getStrengthLevel();
        this.speedLevel = oldData.getSpeedLevel();
        this.healthLevel = oldData.getHealthLevel();
        this.luckLevel = oldData.getLuckLevel();
        this.miningSpeedLevel = oldData.getMiningSpeedLevel();
        this.currentMana = oldData.getCurrentMana();
        this.maxManaLevel = oldData.getMaxManaLevel();
        this.manaRegenLevel = oldData.getManaRegenLevel();
        this.fireballSkillUnlocked = oldData.isFireballSkillUnlocked();
        updateAttributes();
        syncProgressionData();
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void applyCustomMiningSpeed(net.minecraft.block.BlockState block, CallbackInfoReturnable<Float> cir) {
        if (this.miningSpeedLevel > 0) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * (1.0f + (this.miningSpeedLevel * 0.075f))); // +7.5% per level
        }
    }

    /**
     * Hidden Agility jump bonus: each speedLevel adds +0.5% to jump Y-velocity.
     * Not shown in the UI tooltip.
     */
    @Inject(method = "jump", at = @At("RETURN"))
    private void applyAgilityJumpBonus(CallbackInfo ci) {
        if (this.speedLevel > 0) {
            net.minecraft.util.math.Vec3d vel = ((PlayerEntity) (Object) this).getVelocity();
            double bonus = 1.0 + (this.speedLevel * 0.005); // +0.5% per level
            ((PlayerEntity) (Object) this).setVelocity(vel.x, vel.y * bonus, vel.z);
        }
    }

    @Unique
    @SuppressWarnings("resource")
    private void updateAttributes() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient)
            return;

        updateAttribute(player, net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "fcb3d262-6902-4fc8-9993-9c8e2bdceab7", "Strength_Boost", this.strengthLevel * 0.25); // +0.25 dmg per
                                                                                                      // level
        // MULTIPLY_BASE: +2% per level on top of the base speed (0.1).
        // At level 20 => +40% base speed == Speed II potion effect.
        updateAttribute(player, net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "0a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d", "Speed_Boost", this.speedLevel * 0.02,
                net.minecraft.entity.attribute.EntityAttributeModifier.Operation.MULTIPLY_BASE);
        updateAttribute(player, net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH,
                "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", "Health_Boost", this.healthLevel * 1.0); // +0.5 heart (1 HP)
                                                                                                 // per level
        // Luck is handled by LuckOreDropHandler (Fortune-style bonus ore drops).
        // GENERIC_LUCK attribute is intentionally not used here.
    }

    @Unique
    private void updateAttribute(PlayerEntity player, net.minecraft.entity.attribute.EntityAttribute attribute,
            String uuidStr, String name, double value) {
        updateAttribute(player, attribute, uuidStr, name, value,
                net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION);
    }

    @Unique
    private void updateAttribute(PlayerEntity player, net.minecraft.entity.attribute.EntityAttribute attribute,
            String uuidStr, String name, double value,
            net.minecraft.entity.attribute.EntityAttributeModifier.Operation operation) {
        net.minecraft.entity.attribute.EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance != null) {
            java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
            instance.removeModifier(uuid);
            if (value > 0) {
                instance.addPersistentModifier(new net.minecraft.entity.attribute.EntityAttributeModifier(
                        uuid, name, value, operation));
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
    public float getCurrentMana() {
        return currentMana;
    }

    @Override
    public void setCurrentMana(float mana) {
        this.currentMana = Math.max(0.0f, Math.min(mana, getMaxMana()));
        // Note: we might not want to sync the entirety of progression data on every
        // mana change
        // due to network spam. But for now, we'll sync it or rely on a specialized
        // packet later.
        syncProgressionData();
    }

    @Override
    public int getMaxManaLevel() {
        return maxManaLevel;
    }

    @Override
    public void setMaxManaLevel(int level) {
        this.maxManaLevel = level;
        syncProgressionData();
    }

    @Override
    public int getManaRegenLevel() {
        return manaRegenLevel;
    }

    @Override
    public void setManaRegenLevel(int level) {
        this.manaRegenLevel = level;
        syncProgressionData();
    }

    @Override
    public boolean hasEnoughMana(float amount) {
        return ((PlayerEntity) (Object) this).isCreative() || this.currentMana >= amount;
    }

    @Override
    public void consumeMana(float amount) {
        if (!((PlayerEntity) (Object) this).isCreative()) {
            this.currentMana = Math.max(0.0f, this.currentMana - amount);
            syncProgressionData();
        }
    }

    @Unique
    private float getMaxMana() {
        return 50.0f + (this.maxManaLevel * 10.0f); // Base 50, +10 per level (max 250)
    }

    @Unique
    private float getManaRegenPerSecond() {
        return 1.0f + (this.manaRegenLevel * 0.5f); // Base 1 mana/sec, +0.5 per level (max 11/sec)
    }

    @Inject(method = "tick", at = @At("TAIL"))
    @SuppressWarnings("resource")
    private void tickManaRegeneration(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient)
            return;

        float max = getMaxMana();
        if (this.currentMana < max) {
            float regenPerTick = getManaRegenPerSecond() / 20.0f;
            this.currentMana = Math.min(max, this.currentMana + regenPerTick);
            // We should sync this periodically OR when mana reaches full.
            if (player.age % 20 == 0 || this.currentMana >= max) {
                syncProgressionData();
            }
        }
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
        this.maxManaLevel = 0;
        this.manaRegenLevel = 0;
        this.currentMana = 50.0f;
        this.fireballSkillUnlocked = false;
        updateAttributes();
        syncProgressionData();
    }

    @Override
    public void syncProgressionData() {
        if ((Object) this instanceof ServerPlayerEntity serverPlayer) {
            ProgressionPackets.sendSyncPacket(serverPlayer);
        }
    }

    @Override
    public void refreshAttributes() {
        updateAttributes();
    }

    @Override
    public boolean isFireballSkillUnlocked() {
        return fireballSkillUnlocked;
    }

    @Override
    public void setFireballSkillUnlocked(boolean unlocked) {
        this.fireballSkillUnlocked = unlocked;
        syncProgressionData();
    }
}
