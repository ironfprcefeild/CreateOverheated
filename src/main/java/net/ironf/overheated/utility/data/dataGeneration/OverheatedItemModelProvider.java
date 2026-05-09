package net.ironf.overheated.utility.data.dataGeneration;

import net.ironf.overheated.Overheated;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

public class OverheatedItemModelProvider extends ItemModelProvider {
    public OverheatedItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper, Collection<DeferredHolder<Item,? extends Item>> items, HashMap<Supplier<? extends Item>,String> modelOverrides) {
        super(output,Overheated.MODID, existingFileHelper);
        modelOverride = modelOverrides;
        Items = items;
    }


    public Collection<DeferredHolder<Item,? extends Item>> Items;
    public HashMap<Supplier<? extends Item>,String> modelOverride;

    private ItemModelBuilder simpleItem(Item item, @Nullable String textureOverride) {
        return (textureOverride == null)
            ? basicItem(item)
            : getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0",textureOverride);
    }

    @Override
    protected void registerModels() {
        for (DeferredHolder<Item, ? extends Item> I : Items){
            simpleItem(I.get(), modelOverride.getOrDefault(I, null));
        }
    }
}
