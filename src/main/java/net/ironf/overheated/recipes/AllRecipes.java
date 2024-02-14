package net.ironf.overheated.recipes;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.colants.LaserCoolantRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;



public class AllRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Overheated.MODID);
    public static final RegistryObject<RecipeSerializer<LaserCoolantRecipe>> LASER_COOLANT =
            SERIALIZERS.register("laser_cooling", () -> LaserCoolantRecipe.Serializer.INSTANCE);
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }


}
