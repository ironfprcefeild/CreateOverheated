package net.ironf.overheated.metalWorking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.recipes.DummyRecipeInput;
import net.ironf.overheated.recipes.OverheatedCodecs;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;

public class TemperatureTransformRecipe implements Recipe<DummyRecipeInput> {


    public final List<Float> criticalPoints;
    public final NonNullList<UniversalState> phases;

    //The temperature & Coeff this block is considered as for the purpose of machines which diffuse temperature
    public final  List<Float> passiveTemp;
    public final  List<Float> thermalCoeff;
    //When a block equalizes heat with this block, if DTemp * Decay Chance > rand(1,10) and DTemp > critical point, recipe occurs.
    public final float decayChance;

    public TemperatureTransformRecipe(List<Float> criticalPoints, List<Float> passiveTemp, List<Float> thermalCoeff, Float decayChance, NonNullList<UniversalState> phases) {
        this.passiveTemp = passiveTemp;
        this.thermalCoeff = thermalCoeff;
        this.decayChance = decayChance;
        this.criticalPoints = criticalPoints;
        this.phases = phases;
    }
    public TemperatureTransformRecipe(List<Float> criticalPoints, List<Float> passiveTemp, List<Float> thermalCoeff, Float decayChance, Collection<? extends UniversalState> phases) {
        this.passiveTemp = passiveTemp;
        this.thermalCoeff = thermalCoeff;
        this.decayChance = decayChance;
        this.criticalPoints = criticalPoints;
        this.phases = NonNullList.copyOf(phases);
    }



    public List<Float> getPassiveTemp() {
        return passiveTemp;
    }
    public List<Float> getThermalCoeff() {
        return thermalCoeff;
    }
    public float getDecayChance() {
        return decayChance;
    }


    public List<Float> getCriticalPoints() {
        return criticalPoints;
    }

    public NonNullList<UniversalState> getPhases() {
        return phases;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AllRecipes.TEMPERATURE_TRANSFORM.SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AllRecipes.TEMPERATURE_TRANSFORM.TYPE.get();
    }

    /// Serializing
    //Critical points MUST be listed in ascending order, they define the points at which different phases occur
    //The phases are a list of universal states (blockstate or fluid state) which match the criticalpoints, a phase at position i in the list corresponds to the temperautre between criticalpoint[i-1] and ciritcalpoint[i]
    //You MUST include 1 more phase than critical point, (the simplest case being 2 phasesa and 1 critical points)
    //Passive temp and thermal property correspond to the phase at the same index as them
    //Decay chance represents how likeley it is that the phase transition occurs, in other words the speed of it, and is universal across all phase transitions defined by this reicpe
    public static class TemperatureTransformSerializer implements RecipeSerializer<TemperatureTransformRecipe>{
        public static final MapCodec<TemperatureTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                OverheatedCodecs.FLOAT_LIST.fieldOf("critical_points").forGetter(TemperatureTransformRecipe::getCriticalPoints),
                OverheatedCodecs.FLOAT_LIST.fieldOf("passive_temperatures").forGetter(TemperatureTransformRecipe::getCriticalPoints),
                OverheatedCodecs.FLOAT_LIST.fieldOf("thermal_properties").forGetter(TemperatureTransformRecipe::getCriticalPoints),
                Codec.FLOAT.fieldOf("decay_chance").forGetter(TemperatureTransformRecipe::getDecayChance),
                UniversalState.CODEC.listOf().fieldOf("phases").forGetter(TemperatureTransformRecipe::getPhases)
                ).apply(inst, TemperatureTransformRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TemperatureTransformRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        OverheatedCodecs.STREAM_FLOAT_LIST, TemperatureTransformRecipe::getCriticalPoints,
                        OverheatedCodecs.STREAM_FLOAT_LIST, TemperatureTransformRecipe::getPassiveTemp,
                        OverheatedCodecs.STREAM_FLOAT_LIST, TemperatureTransformRecipe::getThermalCoeff,
                        ByteBufCodecs.FLOAT,TemperatureTransformRecipe::getDecayChance,
                        UniversalState.STEAM_CODEC_LIST, TemperatureTransformRecipe::getPhases,
                        TemperatureTransformRecipe::new
                );

        @Override
        public MapCodec<TemperatureTransformRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TemperatureTransformRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }



    ///Nonsense
    @Override
    public boolean matches(DummyRecipeInput dummyRecipeInput, Level level) {
        return true;
    }
    @Override
    public ItemStack assemble(DummyRecipeInput dummyRecipeInput, HolderLookup.Provider provider) {
        return null;
    }
    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }


}
