package net.ironf.overheated.mixin;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.cooling.IAirCurrentReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(com.simibubi.create.content.fluids.spout.SpoutBlockEntity.class)
public class spoutCastingMixin extends SpoutBlockEntity {
    //Degenerate Constructor
    public spoutCastingMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //This injection lets us do stuff whenever affected handlers needs to be updated, which is whenever we want to update some effected block entities
    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    public void tick() {


    }



}
