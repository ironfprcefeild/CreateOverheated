package net.ironf.overheated.utility.data.dataGeneration;

import net.ironf.overheated.Overheated;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

public class OverheatedItemModelProvider extends ItemModelProvider {
    public OverheatedItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper, Collection<RegistryObject<Item>> items,HashMap<RegistryObject<? extends Item>,String> modelOverrides) {
        super(output,Overheated.MODID, existingFileHelper);
        modelOverride = modelOverrides;
        Items = items;
    }


    public Collection<RegistryObject<Item>> Items;
    public HashMap<RegistryObject<? extends Item>,String> modelOverride;

    private ItemModelBuilder simpleItem(Item item, @Nullable String textureOverride) {

        return (textureOverride == null)
            ? basicItem(item)
            : getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0",textureOverride);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Item> I : Items){
            simpleItem(I.get(), modelOverride.getOrDefault(I, null));
        }
    }
}
