package net.ironf.overheated.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(com.simibubi.create.content.fluids.tank.BoilerData.class)
public class boilerDataMixin {

    public int attachedEngines;
    public int attachedWhistles;
    public boolean needsHeatLevelUpdate;

    public boolean evaluate(FluidTankBlockEntity controller) {
        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        int prevEngines = attachedEngines;
        int prevWhistles = attachedWhistles;
        attachedEngines = 0;
        attachedWhistles = 0;


        for (int yOffset = 0; yOffset < controller.getHeight(); yOffset++) {
            for (int xOffset = 0; xOffset < controller.getWidth(); xOffset++) {
                for (int zOffset = 0; zOffset < controller.getWidth(); zOffset++) {
                    BlockPos pos = controllerPos.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidTankBlock.isTank(blockState))
                        continue;
                    for (Direction d : Iterate.directions) {
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        if (AllBlocks.STEAM_ENGINE.has(attachedState) && SteamEngineBlock.getFacing(attachedState) == d)
                            attachedEngines++;
                        if (AllBlocks.STEAM_WHISTLE.has(attachedState)
                                && WhistleBlock.getAttachedDirection(attachedState)
                                .getOpposite() == d)
                            attachedWhistles++;
                        if (net.ironf.overheated.AllBlocks.STEAM_VENT.has(attachedState)
                                && net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock.getAttachedDirection(attachedState)
                                .getOpposite() == d)
                            attachedEngines++;
                    }
                }
            }
        }

        needsHeatLevelUpdate = true;
        return prevEngines != attachedEngines || prevWhistles != attachedWhistles;
    }



}
