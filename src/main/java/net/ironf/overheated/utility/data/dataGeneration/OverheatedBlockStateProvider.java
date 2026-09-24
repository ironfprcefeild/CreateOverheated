package net.ironf.overheated.utility.data.dataGeneration;

import net.ironf.overheated.Overheated;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

public class OverheatedBlockStateProvider extends BlockStateProvider {


    public Collection<DeferredHolder<Block, ? extends Block>> blocks;
    public HashMap<Supplier<? extends Block>,Boolean> makeBlockItems;
    public HashMap<Supplier<? extends Block>,ResourceLocation> modelOverride;
    public ArrayList<DeferredHolder<Block,? extends Block>> TintedBlocks;

    public OverheatedBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper,
                                        Collection<DeferredHolder<Block, ? extends Block>> Blocks,
                                        HashMap<Supplier<? extends Block>,Boolean> MakeBlockItems,
                                        HashMap<Supplier<? extends  Block>,ResourceLocation> ModelOverride,
                                        ArrayList<DeferredHolder<Block,? extends Block>> tintedBlocks) {
        super(output, Overheated.MODID, exFileHelper);
        blocks = Blocks;
        makeBlockItems = MakeBlockItems;
        modelOverride = ModelOverride;
        TintedBlocks = tintedBlocks;

    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile model;
        for (DeferredHolder<Block, ? extends Block> b : blocks) {
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
                    .texture("face",faceLocation)
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
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

}
