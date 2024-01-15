package net.ironf.overheated.steamworks.blocks.turbine.multiblock;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.utility.VecHelper;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class turbineItem extends BlockItem {
    public turbineItem(Block block, Properties properties) {
        super(block, properties);
    }


    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos p_195943_1_, Level p_195943_2_, Player p_195943_3_,
                                                 ItemStack p_195943_4_, BlockState p_195943_5_) {
        MinecraftServer minecraftserver = p_195943_2_.getServer();
        if (minecraftserver == null)
            return false;
        CompoundTag nbt = p_195943_4_.getTagElement("BlockEntityTag");
        if (nbt != null) {
            nbt.remove("Length");
            nbt.remove("Size");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
        }
        return super.updateCustomBlockEntityTag(p_195943_1_, p_195943_2_, p_195943_3_, p_195943_4_, p_195943_5_);
    }

    private void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isShiftKeyDown())
            return;
        Direction face = ctx.getClickedFace();
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (!turbineBlock.isTurbine(placedOnState))
            return;
        turbineBlockEntity turbineAt = ConnectivityHandler.partAt(AllBlockEntities.TURBINE.get(), world, placedOnPos);
        if (turbineAt == null)
            return;

        turbineBlockEntity controllerBE = turbineAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.radius;
        if (width == 1)
            return;

        int tanksToPlace = 0;
        Direction.Axis turbineBlockAxis = turbineBlock.getTurbineBlockAxis(placedOnState);
        if (turbineBlockAxis == null)
            return;
        if (face.getAxis() != turbineBlockAxis)
            return;

        Direction turbineFacing = Direction.fromAxisAndDirection(turbineBlockAxis, Direction.AxisDirection.POSITIVE);
        BlockPos startPos = face == turbineFacing.getOpposite() ? controllerBE.getBlockPos()
                .relative(turbineFacing.getOpposite())
                : controllerBE.getBlockPos()
                .relative(turbineFacing, controllerBE.length);

        if (VecHelper.getCoordinate(startPos, turbineBlockAxis) != VecHelper.getCoordinate(pos, turbineBlockAxis))
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = turbineBlockAxis == Direction.Axis.X ? startPos.offset(0, xOffset, zOffset)
                        : startPos.offset(xOffset, zOffset, 0);
                BlockState blockState = world.getBlockState(offsetPos);
                if (turbineBlock.isTurbine(blockState))
                    continue;
                if (!blockState.getMaterial()
                        .isReplaceable())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = turbineBlockAxis == Direction.Axis.X ? startPos.offset(0, xOffset, zOffset)
                        : startPos.offset(xOffset, zOffset, 0);
                BlockState blockState = world.getBlockState(offsetPos);
                if (turbineBlock.isTurbine(blockState))
                    continue;
                BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
                player.getPersistentData()
                        .putBoolean("SilenceTurbineSound", true);
                super.place(context);
                player.getPersistentData()
                        .remove("SilenceTurbineSound");
            }
        }
    }

}
