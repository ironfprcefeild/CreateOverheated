package net.ironf.overheated.utility.registration;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.GasBlock;
import net.ironf.overheated.gasses.GasFluidSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import static net.ironf.overheated.gasses.GasMapper.GasMap;
import static net.ironf.overheated.gasses.GasMapper.InvGasMap;

public class OverheatedRegistrate extends CreateRegistrate {
    public OverheatedRegistrate(String modid) {
        super(modid);
    }
    public static final DeferredRegister<Block> GAS_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Overheated.MODID);
    public static final DeferredRegister<FluidType> GAS_FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Overheated.MODID);
    public static final DeferredRegister<Fluid> GAS_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Overheated.MODID);
    @Override
    public CreateRegistrate registerEventListeners(IEventBus bus) {
        GAS_BLOCKS.register(bus);
        GAS_FLUID_TYPES.register(bus);
        GAS_FLUIDS.register(bus);
        return super.registerEventListeners(bus);
    }

    public <T extends GasFluidSource, GB extends RegistryObject<? extends GasBlock>> gasEntry<T,GB> gas(String name, NonNullFunction<ForgeFlowingFluid.Properties, T> factory){
        return new gasEntry<T,GB>(name,this,factory);
    }

    public class gasEntry<T extends GasFluidSource, GB extends RegistryObject<? extends GasBlock>> {
        String Name;
        OverheatedRegistrate Parent;
        NonNullFunction<ForgeFlowingFluid.Properties, T> Factory;


        public gasEntry(String name, OverheatedRegistrate parent, NonNullFunction<ForgeFlowingFluid.Properties, T> factory){
            Name = name;
            Parent = parent;
            Factory = factory;
            object(Name);
        }

        public FluidEntry<ForgeFlowingFluid.Flowing> register(RegistryObject<? extends  GasBlock> gasBlock){
            FluidBuilder.FluidTypeFactory fluidType = getFluidFactory(
                    gasBlock,
                    p -> p.supportsBoating(false).viscosity(0).density(1));
            FluidEntry<ForgeFlowingFluid.Flowing> completed = Parent.fluid(Name, fluidType)
                    .properties(b -> b.supportsBoating(false).viscosity(0).density(1))
                    .fluidProperties(p -> p.levelDecreasePerBlock(10).slopeFindDistance(1).tickRate(1))
                    .source(Factory)
                    .bucket()
                    .build()
                    .register();
            GasMap.put(gasBlock,completed);
            InvGasMap.put(completed,gasBlock);
            return completed;
        }
        public FluidBuilder.FluidTypeFactory getFluidFactory(RegistryObject<? extends GasBlock> gasBlock, UnaryOperator<FluidType.Properties> operator) {
            FluidBuilder.FluidTypeFactory factory =
                    (FluidType.Properties properties, ResourceLocation Still_RL, ResourceLocation Flowing_RL) ->
                    (new FluidType(operator.apply(FluidType.Properties.create())) {
                        private final ResourceLocation stillTexture = Still_RL;
                        private final ResourceLocation flowingTexture = Flowing_RL;
                        public final RegistryObject<? extends GasBlock> createdBlock = gasBlock;

                        @Override
                        public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
                            return true;
                        }

                        @Override
                        public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack) {
                            level.setBlockAndUpdate(pos,createdBlock.get().defaultBlockState());
                            level.scheduleTick(pos,createdBlock.get(),2, TickPriority.LOW);
                        }

                        @Override
                        public BlockState getBlockForFluidState(BlockAndTintGetter getter, BlockPos pos, FluidState state) {
                            Overheated.LOGGER.info("Getting block for fluid state");
                            return gasBlock.get().defaultBlockState();
                        }

                        @Override
                        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                            consumer.accept(new IClientFluidTypeExtensions() {
                                @Override
                                public ResourceLocation getStillTexture() {
                                    return stillTexture;
                                }

                                @Override
                                public ResourceLocation getFlowingTexture() {
                                    return flowingTexture;
                                }

                                @Override
                                public ResourceLocation getOverlayTexture() {
                                    return getFlowingTexture();
                                }
                            });
                        }
                    });
            return factory;
        }
    }



    //Registers a gasblock, should be inlined with the register call for gas fluids
    public <T extends GasBlock> gasBlockEntry<T> gasBlock(String name){
        return new gasBlockEntry<T>(name + "_block", this);
    }
    public <T extends GasBlock> gasBlockEntry<T> gasBlock(String name,Supplier<? extends T> Factory){
        return new gasBlockEntry<T>(name + "_block", this,Factory);
    }
    public static class gasBlockEntry<T extends GasBlock>{
        public gasBlockEntry(String name, OverheatedRegistrate parent){
            Name = name;
            Parent = parent;
            useAlt = false;
        }
        public gasBlockEntry(String name, OverheatedRegistrate parent, Supplier<? extends T> Factory){
            Name = name;
            Parent = parent;
            altFactory = Factory;
            useAlt = true;
        }
        String Name;
        OverheatedRegistrate Parent;

        Supplier<? extends T> altFactory;
        boolean useAlt;
        public int shiftChance;
        public int upperTickDelay;
        public int lowerTickDelay;

        public Direction direction;

        public gasBlockEntry<T> defaultFlow(Direction set){
            direction = set;
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

        public RegistryObject<GasBlock> register(){
            return GAS_BLOCKS.register(
                    Name,
                    useAlt ? altFactory :
                    (() -> new GasBlock(
                    BlockBehaviour.Properties.of(Material.AIR)
                            .noOcclusion()
                            .noCollission()
                            .destroyTime(-1)
                            .noLootTable(),shiftChance,lowerTickDelay,upperTickDelay,direction)));

        }



    }
}
