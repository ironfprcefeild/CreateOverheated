package net.ironf.overheated.mixin;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity.class)
public class flywheelMixin {
    protected float lastStressApplied;

    public float calculateStressApplied() {
        float impact = 32f;
        this.lastStressApplied = impact;
        return impact;
    }

}
