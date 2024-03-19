package net.ironf.overheated.utility.registration;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.ironf.overheated.gasses.GasBlock;
import net.ironf.overheated.gasses.GasFluidSource;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static net.ironf.overheated.gasses.GasMapper.GasMap;

public class OverheatedRegistrate extends CreateRegistrate {
    public OverheatedRegistrate(String modid) {
        super(modid);
    }


    public <T extends GasFluidSource, GB extends BlockEntry<? extends GasBlock>> gasEntry<T,GB> gas(String name, NonNullFunction<ForgeFlowingFluid.Properties, T> factory){
        return new gasEntry<T,GB>(name,this,factory);
    }
    public class gasEntry<T extends GasFluidSource, GB extends BlockEntry<? extends GasBlock>> {
        String Name;
        OverheatedRegistrate Parent;
        NonNullFunction<ForgeFlowingFluid.Properties, T> Factory;
        //GB GasBlockRef;

        public gasEntry(String name, OverheatedRegistrate parent, NonNullFunction<ForgeFlowingFluid.Properties, T> factory){
            Name = name;
            Parent = parent;
            Factory = factory;
        }
        /*
        public gasEntry<T,GB> gasBlock(GB gasBlock){
            GasBlockRef = gasBlock;
            return this;
        }

         */
        public FluidEntry<ForgeFlowingFluid.Flowing> register(GB gasBlock){
            GasFluidSource.setGasBlock = gasBlock;
            FluidEntry<ForgeFlowingFluid.Flowing> completed = Parent.standardFluid(Name)
                    .properties(b -> b.supportsBoating(false).viscosity(0).density(GasBlock.setHeavierThanAir ? 1 : -1))
                    .fluidProperties(p -> p.levelDecreasePerBlock(10).slopeFindDistance(1).tickRate(1))
                    .source(Factory)
                    .noBucket()
                    .register();
            GasMap.put(gasBlock,completed);
            return completed;
        }

    }

    //Registers a gasblock, should be inlined with the register call for gas fluids
    public <T extends GasBlock> gasBlockEntry<T> gasBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory){
        return new gasBlockEntry<T>(name + "_block", this, factory);
    }
    public class gasBlockEntry<T extends GasBlock>{
        public gasBlockEntry(String name, OverheatedRegistrate parent, NonNullFunction<BlockBehaviour.Properties, T> factory){
            Name = name;
            Parent = parent;
            Factory = factory;
        }
        String Name;
        OverheatedRegistrate Parent;
        NonNullFunction<BlockBehaviour.Properties, T> Factory;
        public Boolean heavierThanAir = false;
        public int shiftChance;
        public int upperTickDelay;
        public int lowerTickDelay;

        public gasBlockEntry<T> heavierThanAir(){
            heavierThanAir = true;
            return this;
        }
        public gasBlockEntry<T> shiftChance(int s){
            shiftChance = s;
            return this;
        }
        public gasBlockEntry<T> tickDelays(int lower, int upper) {
            upperTickDelay = upper;
            lowerTickDelay = lower;
            return this;
        }

        public BlockEntry<T> register(){
            GasBlock.setHeavierThanAir = heavierThanAir;
            GasBlock.setShiftChance = shiftChance;
            GasBlock.setLowerTickDelay = lowerTickDelay;
            GasBlock.setUpperTickDelay = upperTickDelay;
            return Parent.block(Name, Factory)
                    .properties(p -> p.color(MaterialColor.COLOR_LIGHT_GRAY)
                            .noOcclusion()
                            .noCollission()
                            .destroyTime(-1)
                            .noLootTable())
                    .simpleItem()
                    .register();
        }



    }
}
