package net.ironf.overheated.utility.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.level.block.Block;

public class SimpleBlockStateGenerators {
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleCubeColumn(String path) {
        return (c, p) -> p.simpleBlock(c.get(), p.models()
                .cubeColumn(c.getName(), p.modLoc("block/" + path),p.modLoc("block/" + path +"_top")));
    }
}
