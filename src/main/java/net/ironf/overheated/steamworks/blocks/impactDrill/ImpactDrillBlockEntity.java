package net.ironf.overheated.steamworks.blocks.impactDrill;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.sound.SoundScapes;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.GasMapper;
import net.ironf.overheated.gasses.IGasPlacer;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
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

public class ImpactDrillBlockEntity extends SmartMachineBlockEntity implements ILaserAbsorber, IHaveGoggleInformation, IGasPlacer {
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
    int tickTimer = 20;
    int laserTimer = 50;

    int lastPressure = 0;

    //Each imapact drill takes about 2 steam vents to run :)
    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- == 0) {
            tickTimer = 20;
            extractionTick();
        }
        if (laserTimer > 0) {
            laserTimer--;
        } else {
            currentHeating = 0;
        }
    }

    BlockPos output;
    private void extractionTick() {

        //Get some stuff
        FluidStack contained = tank.getPrimaryHandler().getFluid();
        if (contained.getAmount() < 300) {
            return;
        }
        int pressure = AllSteamFluids.getSteamPressure(contained.getFluid());
        lastPressure = pressure;
        //Ask if fluid is usable
        if (pressure > 0 ) {
            Overheated.LOGGER.info("Fluid is useable");
            //Drain some stuff
            tank.getPrimaryHandler().drain(300, IFluidHandler.FluidAction.EXECUTE);

            //Update some values
            currentTorque += (pressure * 2 * torqueMultiplier());
            currentTorque = Math.min(currentTorque, torqueLimit());

            makeSound(SoundEvents.ARMOR_EQUIP_IRON,2f,0.75f);
            makeSound(AllSoundEvents.STEAM,2f,0.75f);
            particles(output == null ? getBlockPos() : output,level);

            Overheated.LOGGER.info(currentTorque + "/" + torqueLimit() +". Multi of: " + torqueMultiplier());
            BlockPos myPos = getBlockPos();

            //recipe time
            ItemStack inputItem = new ItemStack(level.getBlockState(myPos.below()).getBlock().asItem(), 1);
            Optional<ImpactDrillRecipe> orecipe = grabRecipe(level, inputItem);
            if (orecipe.isPresent()) {
                //We have a recipe, lets do stuff
                ImpactDrillRecipe recipe = orecipe.get();
                //We have the torque and heat, and the gas fits
                if (currentTorque >= recipe.getTorqueNeeded() && currentHeating >= recipe.getHeatNeeded()) {
                    output = getOutputPos();
                    if (output == null)
                        return;

                    currentTorque = currentTorque - recipe.getTorqueImpact();
                    addTemp(recipe.getTorqueImpact());
                    placeGasBlock(output,recipe.getOutput(),level);
                    particles(output,level);
                    particles(output,level);
                    particles(output,level);
                    makeSound(SoundEvents.DRAGON_FIREBALL_EXPLODE,4f,1f);

                }
            }
        }
    }

    //TODO no work
    public void particles(BlockPos mypos, Level level){
        RandomSource rand = level.random;
        level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
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
        level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                mypos.getX() + rand.nextDouble() *2,
                mypos.getY() + 0.1 + rand.nextDouble(),
                mypos.getZ() + rand.nextDouble() *2,
                rand.nextDouble() * 0.04 - 0.02,
                -0.15,
                rand.nextDouble() * 0.04 - 0.02);

    }


    //Returns valid gas output based on casing tower, returns null if a valid output was unable to be found
    public BlockPos getOutputPos() {
        BlockPos atPos = getBlockPos().above();
        while (true) {
            BlockState atState = level.getBlockState(atPos);
            if (atState.getBlock() == Blocks.AIR) {
                return level.isInWorldBounds(atPos) ? atPos : null;
            } else if (atState.getBlock() == AllBlocks.PRESSURIZED_CASING.get()) {
                atPos = atPos.above();
            } else {
                return null;
            }
        }

    }

    public float getAdjustedTemp(){
        if (currentTemp >= 0){
            return 0;
        }
        return Math.min(128,Math.abs(currentTemp));
    }
    public float torqueMultiplier() {
         return (1+(currentHeating/12) * (1+(getAdjustedTemp()/16)));
    }


    public float torqueLimit(){
        return (8 * (1+currentHeating) * (1+(getAdjustedTemp()/8)));
    }

    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat, int d, float eff) {
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

    //Cooling

    @Override
    public boolean doCooling() {
        return true;
    }


    //Read / Writes

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        currentTorque = tag.getFloat("torque");
        currentHeating = tag.getFloat("heat");
        tickTimer = tag.getInt("timer");
        laserTimer = tag.getInt("l_timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("torque",this.currentTorque);
        tag.putFloat("heat",this.currentHeating);
        tag.putInt("timer",this.tickTimer);
        tag.putInt("l_timer",this.laserTimer);
    }

    //Goggle

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.torque").append(easyFloat(currentTorque)).withStyle(WHITE)));
        tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.heat").append(easyFloat(currentHeating)).withStyle(ChatFormatting.RED)));


        if (isPlayerSneaking){
            tempAndCoolInfo(tooltip);

            tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.extra_info")));
            tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.limit_info").append(easyFloat(torqueLimit())),1));
            tooltip.add(addIndent(Component.translatable("coverheated.impact_drill.multiplier_info").append(easyFloat(torqueMultiplier())),1));

        } else {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.crouch_for_more_info")));
        }
        return true;
    }
}
