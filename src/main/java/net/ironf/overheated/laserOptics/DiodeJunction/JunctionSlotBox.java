package net.ironf.overheated.laserOptics.DiodeJunction;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.world.phys.Vec3;

public class JunctionSlotBox extends ValueBoxTransform.Sided {
    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(8, 8, 15.5f);
    }

    @Override
    public float getScale() {
        return super.getScale();
    }
}
