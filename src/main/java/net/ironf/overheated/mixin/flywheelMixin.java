package net.ironf.overheated.mixin;

import com.simibubi.create.api.stress.BlockStressValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity.class)
public class flywheelMixin {
    protected float lastStressApplied;

    public float calculateStressApplied() {
        float impact = 32f;
        this.lastStressApplied = impact;
        return impact;
    }

}
