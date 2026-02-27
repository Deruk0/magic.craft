package com.example.core.progression;

public interface ProgressionData {
    int getCustomExp();

    void setCustomExp(int exp);

    void addCustomExp(int exp);

    int getCustomLevel();

    void setCustomLevel(int level);

    int getStatPoints();

    void setStatPoints(int points);

    int getStrengthLevel();

    void setStrengthLevel(int level);

    int getSpeedLevel();

    void setSpeedLevel(int level);

    int getHealthLevel();

    void setHealthLevel(int level);

    int getLuckLevel();

    void setLuckLevel(int level);

    int getMiningSpeedLevel();

    void setMiningSpeedLevel(int level);

    float getCurrentMana();

    void setCurrentMana(float mana);

    int getMaxManaLevel();

    void setMaxManaLevel(int level);

    int getManaRegenLevel();

    void setManaRegenLevel(int level);

    boolean hasEnoughMana(float amount);

    void consumeMana(float amount);

    boolean isFireballSkillUnlocked();

    void setFireballSkillUnlocked(boolean unlocked);

    void resetProgression();

    void syncProgressionData();

    /**
     * Copy all progression data from another player's ProgressionData.
     * Implemented in PlayerEntityMixin; used on respawn via
     * ServerPlayerEvents.COPY_FROM.
     */
    void copyProgressionFrom(ProgressionData source);

    /**
     * Force re-application of all stat attributes (e.g. after player loads from
     * disk).
     */
    void refreshAttributes();
}
