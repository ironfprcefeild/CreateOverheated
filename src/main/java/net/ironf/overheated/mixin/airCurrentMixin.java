package net.ironf.overheated.mixin;

import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.steamworks.blocks.heatsink.IAirCurrentReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(com.simibubi.create.content.kinetics.fan.AirCurrent.class)
public class airCurrentMixin {
    //Theese are the variables we need to use in the injections
    public final IAirCurrentSource source;
    public Direction direction;
    public float maxDistance;
    public List<BlockEntity> affectedBlockEntities = new ArrayList<>();

    //This constructor should NEVER be called, its just to get rid of a Java Parse error with final objects
    public airCurrentMixin(IAirCurrentSource source) {
        this.source = source;
    }



    //IDK why theese injections wont work, id like them too, they wont.


    //This injection lets us do stuff whenever affected handlers needs to be updated, which is whenever we want to update some effected block entities

    @Inject(method = "findAffectedHandlers", at = @At("HEAD"), remap = false)
    private void findAffectedBlockEntities(CallbackInfo ci) {
        BlockPos start = source.getAirCurrentPos();
        affectedBlockEntities.clear();
        int limit = publicGetLimit();
        for (int i = 1; i <= limit; i++) {
            for (int offset : Iterate.zeroAndOne) {
                BlockPos pos = start.relative(direction, i)
                        .below(offset);
                BlockEntity be = source.getAirCurrentWorld().getBlockEntity(pos);
                if (be instanceof IAirCurrentReader){
                    affectedBlockEntities.add(be);
                }
            }
        }
    }
    //This injection lets us tick our affected BEs
    @Inject(method = "tickAffectedHandlers", at = @At("HEAD"), remap = false)
    private void tickAffectedBlockEntities(CallbackInfo ci) {
        for (BlockEntity be : affectedBlockEntities){
            ((IAirCurrentReader) be).update(source.getSpeed(),source.getAirFlowDirection());
        }
    }

    //A copy of getLimit in air current meant to actually work in a mixin environment
    public int publicGetLimit() {
        if ((float) (int) maxDistance == maxDistance) {
            return (int) maxDistance;
        } else {
            return (int) maxDistance + 1;
        }
    }


}
