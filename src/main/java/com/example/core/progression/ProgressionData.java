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

    void resetProgression();

    void syncProgressionData();
}
