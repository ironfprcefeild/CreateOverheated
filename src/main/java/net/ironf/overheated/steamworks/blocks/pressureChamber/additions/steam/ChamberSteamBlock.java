package net.ironf.overheated.steamworks.blocks.pressureChamber.additions.steam;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChamberSteamBlock extends ChamberAdditionBlock implements IBE<ChamberSteamBlockEntity>, IWrenchable {
    public ChamberSteamBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<ChamberSteamBlockEntity> getBlockEntityClass() {
        return ChamberSteamBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChamberSteamBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHAMBER_STEAM.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        ((ChamberSteamBlockEntity) (context.getLevel().getBlockEntity(context.getClickedPos()))).changeType();
        ChamberCoreBlock.updateChamberState(state, context.getLevel(), context.getClickedPos().relative(getAttachedDirection(state)));
        return InteractionResult.SUCCESS;
    }
}
