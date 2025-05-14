package net.ironf.overheated.utility.registration;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Color;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.GasBlock;
import net.ironf.overheated.gasses.GasFluidSource;
import net.ironf.overheated.worldgen.bedrockDeposits.BedrockDepositFeature;
import net.ironf.overheated.worldgen.saltCaves.SaltCaveFeature;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.*;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static net.ironf.overheated.gasses.GasMapper.*;

public class OverheatedRegistrate extends CreateRegistrate {
    public OverheatedRegistrate(String modid) {
        super(modid);
    }

    @Override
    public String getModid() {
        return Overheated.MODID;
    }

    public static final DeferredRegister<Block> GAS_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Overheated.MODID);
    public static final DeferredRegister<FluidType> GAS_FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Overheated.MODID);
    public static final DeferredRegister<Fluid> GAS_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Overheated.MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Overheated.MODID);

    public static final DeferredRegister<Block> FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,Overheated.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES,Overheated.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,Overheated.MODID);
    public static final DeferredRegister<Item> BUCKET_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,Overheated.MODID);

    @Override
    public CreateRegistrate registerEventListeners(IEventBus bus) {
        GAS_BLOCKS.register(bus);
        GAS_FLUID_TYPES.register(bus);
        GAS_FLUIDS.register(bus);
        FEATURES.register(bus);

        FLUID_BLOCKS.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BUCKET_ITEMS.register(bus);

        return super.registerEventListeners(bus);
    }

    public <T extends GasFluidSource, GB extends RegistryObject<? extends GasBlock>> gasEntry<T,GB> gas(String name, NonNullFunction<ForgeFlowingFluid.Properties, T> factory){
        return new gasEntry<T,GB>(name,this,factory);
    }

    public class gasEntry<T extends GasFluidSource, GB extends RegistryObject<? extends GasBlock>> {
        String Name;
        OverheatedRegistrate Parent;
        NonNullFunction<ForgeFlowingFluid.Properties, T> Factory;
        String blockTextureOver;
        String bucketTextureOver;
        int density;

        public gasEntry(String name, OverheatedRegistrate parent, NonNullFunction<ForgeFlowingFluid.Properties, T> factory){
            Name = name;
            Parent = parent;
            Factory = factory;
            density = -1;
            blockTextureOver = null;
            object(Name);
        }

        public gasEntry<T,GB> Density(int d){
            this.density = d;
            return this;
        }

        public gasEntry<T,GB> GasTextures(String location){
            this.blockTextureOver = location;
            return this;
        }
        public gasEntry<T,GB> BucketTextures(String location){
            this.bucketTextureOver = location;
            return this;
        }

        public gasEntry<T,GB> basicTexturing(){
            this.BucketTextures(Name);
            return this.GasTextures(Name);
        }

        public gasEntry<T,GB> overrideTexturing(String location){
            this.BucketTextures(location);
            return this.GasTextures(location);
        }


        public FluidEntry<ForgeFlowingFluid.Flowing> register(RegistryObject<? extends  GasBlock> gasBlock){
            FluidBuilder.FluidTypeFactory fluidType = getFluidFactory(
                    gasBlock,
                    p -> p.supportsBoating(false).viscosity(0).density(density),
                    blockTextureOver == null ? null : new ResourceLocation(Parent.getModid(),blockTextureOver));
            FluidEntry<ForgeFlowingFluid.Flowing> completed = Parent.fluid(Name, fluidType)
                    .properties(b -> b.supportsBoating(false).viscosity(0).density(density))
                    .fluidProperties(p -> p.levelDecreasePerBlock(10).slopeFindDistance(1).tickRate(1))
                    .source(Factory)
                    .bucket()
                        .properties(p -> p.craftRemainder(Items.BUCKET).stacksTo(1))
                        .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation(getModid(), "item/" + (bucketTextureOver == null ? Name : bucketTextureOver) + "_bucket")))
                        .build()
                    .block().blockstate(simpleGasAll(blockTextureOver)).build()
                    .register();
            GasMap.put(gasBlock,completed);
            InvGasMap.put(completed,gasBlock);
            return completed;
        }

        public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleGasAll(String path) {
            return (c, p) -> p.simpleBlock(c.get(), p.models().cubeAll(c.getName(), p.modLoc("gas/" + path)));
        }

        public FluidBuilder.FluidTypeFactory getFluidFactory(RegistryObject<? extends GasBlock> gasBlock, UnaryOperator<FluidType.Properties> operator, ResourceLocation overRL) {
        FluidBuilder.FluidTypeFactory factory =
                (FluidType.Properties properties, ResourceLocation Still_RL, ResourceLocation Flowing_RL) ->
                (new FluidType(operator.apply(FluidType.Properties.create())) {
                    private final ResourceLocation stillTexture = overRL == null ? Still_RL : overRL;
                    private final ResourceLocation flowingTexture = overRL == null ? Flowing_RL : overRL;
                    public final RegistryObject<? extends GasBlock> createdBlock = gasBlock;

                    @Override
                    public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
                        return true;
                    }
                    @Override
                    public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack) {
                        level.setBlockAndUpdate(pos,createdBlock.get().defaultBlockState());
                        level.scheduleTick(pos,createdBlock.get(),2, TickPriority.NORMAL);
                    }
                    @Override
                    public BlockState getBlockForFluidState(BlockAndTintGetter getter, BlockPos pos, FluidState state) {
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

        String textureOver = null;

        Supplier<? extends T> altFactory;
        boolean useAlt;
        public int shiftChance;
        public int upperTickDelay;
        public int lowerTickDelay;

        public int pressurizeChance;
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
        //1 is maximum, the explosion risk is equal 1/(safety). A 0 or less indicates perfectly safe
        public gasBlockEntry<T> explosionSafety(int safety) {
            if (safety == 0){
                pressurizeChance = -1;
            } else {
                pressurizeChance = Math.max(safety, -1);
            }
            return this;
        }
        public gasBlockEntry<T> overideTexturing(String location){
            this.textureOver = location;
            return this;
        }
        public RegistryObject<GasBlock> register(){
            return GAS_BLOCKS.register(
                    Name,
                    useAlt ? altFactory :
                    (() -> new GasBlock(
                    BlockBehaviour.Properties.of()
                            .noOcclusion()
                            .noCollission()
                            .replaceable()
                            .destroyTime(-1)
                            .sound(SoundType.FUNGUS)
                            .isSuffocating(OverheatedRegistrate::always)
                            .noLootTable(),shiftChance, pressurizeChance, lowerTickDelay,upperTickDelay,direction)));

        }

    }
    private static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }


    ///Deposits

    public depositFeatureBuilder depositFeature(String name){
        return new depositFeatureBuilder(name, this);
    }

    public static class depositFeatureBuilder {
        String Name;
        BlockEntry<? extends Block> Block;

        BlockEntry<? extends Block> EncasedBlock;
        int frequency;
        int sizeLower;
        int sizeUpper;
        int borderSize;
        towerSizeGetter getter;

        OverheatedRegistrate Parent;
        public depositFeatureBuilder(String name, OverheatedRegistrate parent){
            Name = name;
            Parent = parent;
            frequency = 64;
            sizeLower = 8;
            sizeUpper = 16;
            borderSize = 3;
            getter = (size, isTower, rand) -> isTower ?  rand.nextIntBetweenInclusive(size, size * 2) : rand.nextIntBetweenInclusive(size + 5, size * 3);
        }

        public depositFeatureBuilder Frequency(int chunksPerDeposit){
            frequency = chunksPerDeposit;
            return this;
        }

        public depositFeatureBuilder Size(int lowerBound, int upperBound){
            sizeLower = lowerBound;
            sizeUpper = upperBound;
            return this;
        }

        public depositFeatureBuilder makeBlock(BlockEntry<? extends Block> b){
            Block = b;
            return this;
        }
        public depositFeatureBuilder makeEncasedBlock(BlockEntry<? extends Block> b){
            EncasedBlock = b;
            return this;
        }

        public depositFeatureBuilder BorderSize(int BorderSize){
            this.borderSize = BorderSize;
            return this;
        }

        //The function is a functionalInterface which takes in the current rand source and size of a generated deposit and returns a number for how tall a pillar
        //Above that deposit (made of the encased block) should be. It should be random,
        public depositFeatureBuilder TowerSizeGenerator(towerSizeGetter function){
            this.getter = function;
            return this;
        }

        public RegistryObject<BedrockDepositFeature> register(){
            return FEATURES.register(Name,() -> new BedrockDepositFeature(NoneFeatureConfiguration.CODEC, sizeLower, sizeUpper, frequency, getter, borderSize, Block, EncasedBlock));
        }

    }

    public interface towerSizeGetter{
        int get(int size, boolean isEdgeTower, RandomSource rand);
    }

    ///Salt Caves

    public saltCaveFeatureBuilder saltCaveFeature(String name){
        return new saltCaveFeatureBuilder(name, this);
    }

    public static class saltCaveFeatureBuilder {
        String Name;
        BlockEntry<? extends Block> Block;
        BlockEntry<? extends Block> CrystalBlock;
        int frequency;
        int sizeLower;
        int sizeUpper;
        int shellHeight;
        int crystalSizeLower;
        int crystalSizeUpper;

        float crystalFrequency;
        OverheatedRegistrate Parent;
        public saltCaveFeatureBuilder(String name, OverheatedRegistrate parent){
            Name = name;
            Parent = parent;
            frequency = 64;
            sizeLower = 40;
            sizeUpper = 50;
            crystalFrequency = 0.25f;
            shellHeight = 4;
            crystalSizeLower = 3;
            crystalSizeUpper = 10;
        }

        public saltCaveFeatureBuilder Frequency(int chunksPerDeposit){
            frequency = chunksPerDeposit;
            return this;
        }

        public saltCaveFeatureBuilder Size(int lowerBound, int upperBound){
            sizeLower = lowerBound;
            sizeUpper = upperBound;
            return this;
        }

        public saltCaveFeatureBuilder makeShellBlock(BlockEntry<? extends Block> b){
            Block = b;
            return this;
        }
        public saltCaveFeatureBuilder makeCrystalBlock(BlockEntry<? extends Block> b){
            CrystalBlock = b;
            return this;
        }

        public saltCaveFeatureBuilder shellHeight(int shellHeight){
            this.shellHeight = (int) (double) (shellHeight / 2);
            return this;
        }

        public saltCaveFeatureBuilder crystalFrequency(float frequency){
            this.crystalFrequency = frequency;
            return this;
        }

        public saltCaveFeatureBuilder crystalSizes(int lower, int upper){
            this.crystalSizeUpper = upper;
            this.crystalSizeLower = lower;
            return this;
        }

        public RegistryObject<SaltCaveFeature> register(){
            return FEATURES.register(Name,() -> new SaltCaveFeature(NoneFeatureConfiguration.CODEC, sizeLower, sizeUpper, frequency, crystalFrequency, shellHeight, crystalSizeUpper, crystalSizeLower, CrystalBlock, Block));
        }

    }


    public FluidRegistration SimpleFluid(String name){
        return new FluidRegistration(this,name);
    }
    public static List<RegistryObject<? extends Item>> allBuckets = new ReferenceArrayList<>();
    public static class FluidRegistration {
        OverheatedRegistrate Parent;
        String name;

        public RegistryObject<FlowingFluid> FLOWING;
        public RegistryObject<FlowingFluid> SOURCE;
        public ForgeFlowingFluid.Properties FLUID_PROPERTIES;
        public RegistryObject<FluidType> FLUID_TYPE;

        public RegistryObject<BucketItem> BUCKET;
        public RegistryObject<LiquidBlock> FLUID_BLOCK;

        private int tintColor = 0x252FBE;
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1;
        private int tickRate = 5;

        public String BucketModelLocation;
        public BlockBehaviour.Properties block_properties = BlockBehaviour.Properties.copy(Blocks.WATER);

        public FluidRegistration(OverheatedRegistrate parent, String Name){
            name = Name;
            BucketModelLocation = Name;
            Parent = parent;
        }

        public FluidRegistration bucketModelLocation(String set){
            BucketModelLocation = set;
            return this;
        }

        public FluidRegistration blockProperties(BlockBehaviour.Properties set){
            block_properties = set;
            return this;
        }
        public FluidRegistration slopeFindDistance(int a){
            slopeFindDistance = a;
            return this;
        }
        public FluidRegistration levelDecreasePerBlock(int a){
            levelDecreasePerBlock = a;
            return this;
        }
        public FluidRegistration explosionResistance(float a){
            explosionResistance = a;
            return this;
        }
        public FluidRegistration tickRate(int a){
            tickRate = a;
            return this;
        }
        public FluidRegistration tintColor(int a){
            tintColor = a;
            return this;
        }

        public FluidRegistration Register(UnaryOperator<FluidType.Properties> fluidTypeProperties) {

            ResourceLocation textureLocation = new ResourceLocation(Parent.getModid(),"block/fluids/" + name );

            FLUID_TYPE = registerFluidType(name,
                    fluidTypeProperties,
                    textureLocation,textureLocation,textureLocation
                    ,tintColor);


            FLOWING = FLUIDS.register("flowing_" + name, () -> new ForgeFlowingFluid.Flowing(FLUID_PROPERTIES));
            SOURCE = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(FLUID_PROPERTIES));

            BUCKET = registerBucket(BucketModelLocation,SOURCE);

            FLUID_BLOCK = FLUID_BLOCKS.register(name + "_fluid_block",
                    () -> new LiquidBlock(SOURCE, block_properties));

            FLUID_PROPERTIES = new ForgeFlowingFluid
                    .Properties(FLUID_TYPE,SOURCE,FLOWING)
                    .explosionResistance(explosionResistance).tickRate(tickRate).slopeFindDistance(slopeFindDistance).levelDecreasePerBlock(levelDecreasePerBlock)
                    .block(FLUID_BLOCK).bucket(BUCKET);

            return this;
        }
        public static RegistryObject<BucketItem> registerBucket(String fluidName, RegistryObject<FlowingFluid> fluid){
            RegistryObject<BucketItem> toReturn = BUCKET_ITEMS.register(fluidName + "_bucket", () -> new BucketItem(fluid,new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            allBuckets.add(toReturn);
            return toReturn;
        }
        private static RegistryObject<FluidType> registerFluidType(String name, UnaryOperator<FluidType.Properties> operator, ResourceLocation Still_RL, ResourceLocation Flowing_RL, ResourceLocation Overlay_RL, int Tint_Color) {
            return FLUID_TYPES.register(name, () -> new FluidType(operator.apply(FluidType.Properties.create())) {

                private final ResourceLocation stillTexture = Still_RL;
                private final ResourceLocation flowingTexture = Flowing_RL;
                private final ResourceLocation overlayTexture = Overlay_RL;

                private final int tintColor = Tint_Color;

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
                            return overlayTexture;
                        }

                        @Override
                        public int getTintColor() {
                            return tintColor;
                        }
                    });
                }
            });
        }

    }


    //Connected Textures
    public static <T extends Block> NonNullConsumer<? super T> easyConnectedTextures(CTSpriteShiftEntry shiftEntry){
        return connectedTextures(() -> new SimpleCTBehaviour(shiftEntry));
    }

}
