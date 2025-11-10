package net.ironf.overheated.batteries.discharger;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.world.phys.Vec3;

public class DischargerSlotBox extends ValueBoxTransform.Sided {
    @Override
    protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 11f, 15.5f);
    }

    @Override
    public float getScale() {
        return 0.5f;
    }

}
