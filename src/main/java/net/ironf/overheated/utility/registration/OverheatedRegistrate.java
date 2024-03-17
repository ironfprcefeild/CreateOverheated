package net.ironf.overheated.utility.registration;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.ironf.overheated.utility.registration.gasses.GasBlock;
import net.ironf.overheated.utility.registration.gasses.GasFluidSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class OverheatedRegistrate extends CreateRegistrate {
    public OverheatedRegistrate(String modid) {
        super(modid);
    }

    public gasBuilder Gas(String name){
        return new gasBuilder(name, this);
    }
    public class gasBuilder{
        String Name;
        OverheatedRegistrate Parent;

        public gasBuilder(String name, OverheatedRegistrate parent){
            Name = name;
            Parent = parent;
        }

        public FluidEntry<ForgeFlowingFluid.Flowing> register(BlockEntry<? extends GasBlock> gasBlock, NonNullFunction<ForgeFlowingFluid.Properties, ? extends GasFluidSource> sourceFactory){
            GasFluidSource.setGasBlock = gasBlock;
            return Parent.standardFluid(Name)
                    .properties(b -> b.supportsBoating(false).viscosity(0))
                    .fluidProperties(p -> p.levelDecreasePerBlock(10).slopeFindDistance(1).tickRate(1))
                    .source(sourceFactory)
                    .noBucket()
                    .register();
        }

    }
    public gasBlockBuilder gasBlock(String name){
        return new gasBlockBuilder(name, this);
    }

    public class gasBlockBuilder{
        public gasBlockBuilder(String name, OverheatedRegistrate parent){
            Name = name;
            Parent = parent;
        }
        String Name;
        OverheatedRegistrate Parent;

        public Boolean heavierThanAir = false;
        public int shiftChance;
        public int upperTickDelay;
        public int lowerTickDelay;

        public gasBlockBuilder heavierThanAir(){
            heavierThanAir = true;
            return this;
        }
        public gasBlockBuilder shiftChance(int s){
            shiftChance = s;
            return this;
        }
        public gasBlockBuilder tickDelays(int lower, int upper) {
            upperTickDelay = upper;
            lowerTickDelay = lower;
            return this;
        }

        public BlockEntry<? extends GasBlock> register(NonNullFunction<BlockBehaviour.Properties, ? extends GasBlock> sourceFactory){
            GasBlock.setHeavierThanAir = heavierThanAir;
            GasBlock.setShiftChance = shiftChance;
            GasBlock.setLowerTickDelay = lowerTickDelay;
            GasBlock.setUpperTickDelay = upperTickDelay;
            return Parent.block(Name, sourceFactory)
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
