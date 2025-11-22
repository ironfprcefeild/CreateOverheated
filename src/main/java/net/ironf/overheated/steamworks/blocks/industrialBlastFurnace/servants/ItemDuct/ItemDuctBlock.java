package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.ItemDuct;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ItemDuctBlock extends Block implements IBE<ItemDuctBlockEntity> {
    public ItemDuctBlock(Properties properties) {
        super(properties);
    }


    @Override
    public Class<ItemDuctBlockEntity> getBlockEntityClass() {
        return ItemDuctBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ItemDuctBlockEntity> getBlockEntityType() {
        return AllBlockEntities.ITEM_DUCT.get();
    }




}
