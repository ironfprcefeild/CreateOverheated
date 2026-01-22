package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.fluidDuct;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FluidDuctBlock extends Block implements IBE<FluidDuctBlockEntity>, IWrenchable {
    public FluidDuctBlock(Properties properties) {
        super(properties);
    }


    @Override
    public Class<FluidDuctBlockEntity> getBlockEntityClass() {
        return FluidDuctBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidDuctBlockEntity> getBlockEntityType() {
        return AllBlockEntities.FLUID_DUCT.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockEntity BE = context.getLevel().getBlockEntity(context.getClickedPos());
        if (BE instanceof FluidDuctBlockEntity duct) {
            duct.wrench();
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }
}
