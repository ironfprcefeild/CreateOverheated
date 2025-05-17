package net.ironf.overheated.utility.data.dataGeneration;

import net.ironf.overheated.Overheated;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.HashMap;

public class OverheatedBlockStateProvider extends BlockStateProvider {


    public Collection<RegistryObject<Block>> blocks;
    public HashMap<RegistryObject<? extends Block>,Boolean> makeBlockItems;
    public HashMap<RegistryObject<? extends Block>,ResourceLocation> modelOverride;

    public OverheatedBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper,
                                        Collection<RegistryObject<Block>> Blocks,
                                        HashMap<RegistryObject<? extends Block>,Boolean> MakeBlockItems,
                                        HashMap<RegistryObject<? extends  Block>,ResourceLocation> ModelOverride) {
        super(output, Overheated.MODID, exFileHelper);
        blocks = Blocks;
        makeBlockItems = MakeBlockItems;
        modelOverride = ModelOverride;

    }

    @Override
    protected void registerStatesAndModels() {
            for (RegistryObject<Block> b : blocks) {
                ModelFile model = modelOverride.containsKey(b) ?
                        models().cubeAll(name(b.get()), modelOverride.get(b))  :
                        cubeAll(b.get());

                if (makeBlockItems.get(b)) {
                    simpleBlockWithItem(b.get(), model);
                } else {
                    simpleBlock(b.get(),model);
                }
            }
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

}
