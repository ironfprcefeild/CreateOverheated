package net.ironf.overheated.utility.data.dataGeneration;

import net.ironf.overheated.Overheated;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class OverheatedBlockStateProvider extends BlockStateProvider {


    public Collection<RegistryObject<Block>> blocks;
    public HashMap<RegistryObject<? extends Block>,Boolean> makeBlockItems;
    public HashMap<RegistryObject<? extends Block>,ResourceLocation> modelOverride;
    public ArrayList<RegistryObject<? extends Block>> TintedBlocks;

    public OverheatedBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper,
                                        Collection<RegistryObject<Block>> Blocks,
                                        HashMap<RegistryObject<? extends Block>,Boolean> MakeBlockItems,
                                        HashMap<RegistryObject<? extends  Block>,ResourceLocation> ModelOverride,
                                        ArrayList<RegistryObject<? extends Block>> tintedBlocks) {
        super(output, Overheated.MODID, exFileHelper);
        blocks = Blocks;
        makeBlockItems = MakeBlockItems;
        modelOverride = ModelOverride;
        TintedBlocks = tintedBlocks;

    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile model;
        for (RegistryObject<Block> b : blocks) {
            String faceLocation = (modelOverride.containsKey(b) ?  modelOverride.get(b).toString() : "block/"+name(b.get()));
            if (TintedBlocks.contains(b)){
                model = models().getBuilder(name(b.get()))
                    .element()
                        .face(Direction.UP)
                            .tintindex(0)
                            .cullface(Direction.UP)
                            .end()
                        .face(Direction.DOWN)
                            .tintindex(0)
                            .cullface(Direction.DOWN)
                            .end()
                        .face(Direction.NORTH)
                            .tintindex(0)
                            .cullface(Direction.NORTH)
                            .end()
                        .face(Direction.EAST)
                            .tintindex(0)
                            .cullface(Direction.EAST)
                            .end()
                        .face(Direction.SOUTH)
                            .tintindex(0)
                            .cullface(Direction.SOUTH)
                            .end()
                        .face(Direction.WEST)
                            .tintindex(0)
                            .cullface(Direction.WEST)
                            .end()
                        .textureAll("#face")
                    .end()
                    .texture("#face",faceLocation)
                    .parent(new ModelFile.UncheckedModelFile("block/block"))
                    .renderType("translucent");
            } else {
                model = modelOverride.containsKey(b) ?
                        models().cubeAll(name(b.get()), modelOverride.get(b)) :
                        cubeAll(b.get());
            }

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
