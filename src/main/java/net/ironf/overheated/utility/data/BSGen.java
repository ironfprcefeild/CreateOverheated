package net.ironf.overheated.utility.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface BSGen {
    String getModel(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, BlockState state);
}
