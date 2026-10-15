package net.ironf.overheated.metalWorking;

public class ThermalInformationBundle {
    public final PhaseTransformBundle phases;
    public final float passiveTemp;
    public final float thermalProperty;
    public final float decayChance;

    public ThermalInformationBundle(PhaseTransformBundle phases, float passiveTemp, float thermalProperty, float decayChance) {
        this.phases = phases;
        this.passiveTemp = passiveTemp;
        this.thermalProperty = thermalProperty;
        this.decayChance = decayChance;
    }
}
