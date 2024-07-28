package net.ironf.overheated.steamworks.blocks.impactDrill;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.gasses.GasMapper;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.heatsink.HeatSinkHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.easyFloat;
import static net.minecraft.ChatFormatting.WHITE;

public class ImpactDrillBlockEntity extends SmartBlockEntity implements ILaserAbsorber, HeatSinkHelper, IHaveGoggleInformation {
    public ImpactDrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //Setting up item / fluid handling


    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 600));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

    //Doing stuff

    float currentTorque = 0;
    float currentHeating = 0;
    float lastHeatSink = 0;
    int tickTimer = 20;
    int laserTimer = 50;

    int lastPressure = 0;

    //Each imapact drill takes about 2 steam vents to run :)
    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- == 0) {
            tickTimer = 20;
            lastHeatSink = getHeatSunkenFrom(getBlockPos(), level);
            extractionTick();
        }
        if (laserTimer > 0) {
            laserTimer--;
        } else {
            currentHeating = 0;
        }
    }

    private void extractionTick() {

        //Get some stuff
        FluidStack contained = tank.getPrimaryHandler().getFluid();
        if (contained.getAmount() < 300) {
            return;
        }
        int pressure = AllSteamFluids.getSteamPressure(contained.getFluid());
        lastPressure = pressure;
        //Ask if fluid is usable
        if (pressure > 0) {
            //Drain some stuff
            tank.getPrimaryHandler().drain(300, IFluidHandler.FluidAction.EXECUTE);

            //Update some values
            currentTorque += (pressure * torqueMultiplier());
            currentTorque = Math.min(currentTorque, torqueLimit());
            BlockPos myPos = getBlockPos();

            //recipe time
            ItemStack inputItem = new ItemStack(level.getBlockState(myPos.below()).getBlock().asItem(), 1);
            Optional<ImpactDrillRecipe> orecipe = grabRecipe(level, inputItem);
            if (orecipe.isPresent()) {
                //We have a recipe, lets do stuff
                ImpactDrillRecipe recipe = orecipe.get();

                //We have the torque and the gas fits
                if (currentTorque >= recipe.getTorqueNeeded() && currentHeating >= recipe.getHeatNeeded()) {
                    BlockPos output = getOutputPos();
                    if (output == null)
                        return;
                    currentTorque = currentTorque - recipe.getTorqueImpact();
                    level.setBlockAndUpdate(output, GasMapper.InvFluidGasMap.get(recipe.getOutput().getFluid().getFluidType()).get().defaultBlockState());
                    particles(output);
                }
            }
        }
    }

    public void particles(BlockPos outputPos){
        BlockPos mypos = getBlockPos();
        RandomSource rand = level.random;

        level.addParticle(ParticleTypes.EXPLOSION,
                mypos.getX() + rand.nextDouble(),
                mypos.getY() - 0.5 + rand.nextDouble(),
                mypos.getZ() + rand.nextDouble(),
                rand.nextDouble() * 0.04 - 0.02,
                0.3,
                rand.nextDouble() * 0.04 - 0.02);
        level.addParticle(ParticleTypes.EXPLOSION,
                mypos.getX() + rand.nextDouble() *2,
                mypos.getY() - 0.5 + rand.nextDouble(),
                mypos.getZ() + rand.nextDouble() *2,
                rand.nextDouble() * 0.04 - 0.02,
                0.3,
                rand.nextDouble() * 0.04 - 0.02);
        level.addParticle(ParticleTypes.SMOKE,
                outputPos.getX() + rand.nextDouble() *2,
                outputPos.getY() + 0.1 + rand.nextDouble(),
                outputPos.getZ() + rand.nextDouble() *2,
                rand.nextDouble() * 0.04 - 0.02,
                -0.15,
                rand.nextDouble() * 0.04 - 0.02);
        level.playLocalSound(mypos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS,1.0f,1.0f,false);
    }

    //Returns valid gas output based on impact tubing, returns null if one was unable to be found
    private BlockPos getOutputPos() {
        BlockPos atPos = getBlockPos().above();
        while (true) {
            BlockState atState = level.getBlockState(atPos);
            if (atState == Blocks.AIR.defaultBlockState()) {
                return atPos;
            } else if (atState == AllBlocks.IMPACT_TUBING.getDefaultState()) {
                atPos = atPos.above();
            } else {
                return null;
            }
        }

    }

    public float torqueMultiplier() {
         return 1+currentHeating /16+lastHeatSink /1024;
    }


    public float torqueLimit(){
        return 1 + (Math.max(1,currentHeating) * Math.max(1,lastHeatSink / 64));
    }

    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat, int d) {
        currentHeating = Math.min(beamHeat.getTotalHeat(),16);
        laserTimer = 30;
        return false;
    }

    public static Optional<ImpactDrillRecipe> grabRecipe(Level level, ItemStack stack){
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, stack);
        Optional<ImpactDrillRecipe> recipe = level.getRecipeManager().getRecipeFor(ImpactDrillRecipe.Type.INSTANCE,inventory, level);
        return recipe;
    }

    //Read / Writes

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        currentTorque = tag.getFloat("torque");
        currentHeating = tag.getFloat("heat");
        lastHeatSink = tag.getFloat("last_heat_sink");
        tickTimer = tag.getInt("timer");
        laserTimer = tag.getInt("l_timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("torque",this.currentTorque);
        tag.putFloat("heat",this.currentHeating);
        tag.putFloat("last_heat_sink",this.lastHeatSink);
        tag.putInt("timer",this.tickTimer);
        tag.putInt("l_timer",this.laserTimer);
    }

    //Goggle

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.torque").append(easyFloat(currentTorque)).withStyle(WHITE)));
        tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.airflow").withStyle(ChatFormatting.WHITE)));
        tooltip.add(addIndent(Component.literal(easyFloat(lastHeatSink)).withStyle(ChatFormatting.AQUA), 1));
        tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.heat").append(easyFloat(currentHeating)).withStyle(ChatFormatting.RED)));
        if (isPlayerSneaking){
            tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.extra_info")));
            tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.limit_info").append(easyFloat(torqueLimit())),1));
            tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.multiplier_info").append(easyFloat(torqueMultiplier())),1));

        } else {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.crouch_for_more_info")));
        }
        return true;
    }
}
