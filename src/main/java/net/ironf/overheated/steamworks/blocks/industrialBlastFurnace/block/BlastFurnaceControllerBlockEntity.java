package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.createmod.catnip.outliner.Outliner;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.IGasPlacer;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.BlastFurnaceStatus;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.multiblock.BlastFurnaceMultiblock;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.multiblock.MultiblockData;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.multiblock.MultiblockResult;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.recipe.IndustrialBlastingRecipe;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.recipe.IndustrialMeltingRecipe;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.BlastFurnaceServantBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

/// TODO Goggles don't work. Fluids arent saved on reload (idk why). And fluid extraction with filters is bugged
public class BlastFurnaceControllerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IGasPlacer {

    public BlastFurnaceControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        MainTank = new BlastFurnaceTank(this);

        Inventory = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        InputLazyItemHandler = LazyOptional.empty();

    }

    /// Multiblock
    MultiblockData MBData = null;
    MultiblockResult lastAssemblyResult = MultiblockResult.ERROR(null,"block.coverheated.multiblock.error.unassembled");

    //This gathers new information about the size and assigns servant BE's.
    public void updateMultiblock(boolean forceCheckSize){
        //Overheated.LOGGER.info("updating multiblock");

        //Decouple Servants
        ArrayList<BlockPos> oldServants = new ArrayList<>();
        if (MBData != null){
            oldServants.addAll(MBData.servantPositions);
        }

        BlastFurnaceMultiblock bfm = new BlastFurnaceMultiblock();

        //This will be null if we failed to assemble the multiblock.
        //This will recouple servants if we succeeded
        MBData = bfm.assembleMultiblock(level,this);
        lastAssemblyResult = bfm.status;
        if (!lastAssemblyResult.success()){
            //We failed to assemble, servants should no longer be able to access this
            for (BlockPos blockPos : oldServants) {
                BlockEntity BE = level.getBlockEntity(blockPos);
                if (BE instanceof BlastFurnaceServantBlockEntity servantBE) {
                    servantBE.updateController(null);
                }
            }

            return;
        }

        for (BlockPos blockPos : oldServants){
            //unbind if no longer a servant
            //new servants are added and bound when creating the multiblock
            if (!MBData.servantPositions.contains(blockPos)){
                BlockEntity BE = level.getBlockEntity(blockPos);
                if (BE instanceof BlastFurnaceServantBlockEntity servantBE) {
                    servantBE.updateController(null);
                }
            }
        }

        //Resolve fluid tanks for new size
        int newSize = MBData.innerArea();
        if (newSize != currentSize || forceCheckSize){
            currentSize = newSize;

            MainTank.setCapacity(currentSize * 6000);

            FluidStack oldSteam = SteamTank.getPrimaryHandler().getFluid();
            SteamTank.getPrimaryHandler().setFluid(new FluidStack(oldSteam.getFluid(),Math.min(oldSteam.getAmount(),currentSize*4000)));
            SteamTank.getPrimaryHandler().setCapacity(currentSize * 2000);

            FluidStack oldOxygen = OxygenTank.getPrimaryHandler().getFluid();
            OxygenTank.getPrimaryHandler().setFluid(new FluidStack(oldSteam.getFluid(),Math.min(oldOxygen.getAmount(),currentSize*4000)));
            OxygenTank.getPrimaryHandler().setCapacity(currentSize * 2000);
            
        }
        //Overheated.LOGGER.info(MBData.servantPositions.size()+"");
    }

    public void removeServant(BlockPos bp){
        MBData.servantPositions.remove(bp);
    }

    /// Fluid Handlers
    public LazyOptional<IFluidHandler> SteamLazyFluidHandler  = LazyOptional.empty();
    public LazyOptional<IFluidHandler> OxygenLazyFluidHandler  = LazyOptional.empty();


    public LazyOptional<IFluidHandler> MainTankFluidHandler = LazyOptional.empty();

    public SmartFluidTankBehaviour SteamTank;
    public SmartFluidTankBehaviour OxygenTank;

    public BlastFurnaceTank MainTank;


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(SteamTank = SmartFluidTankBehaviour.single(this, 0));
        behaviours.add(OxygenTank = SmartFluidTankBehaviour.single(this, 0));
        behaviours.add(MainTank = new BlastFurnaceTank(this));
    }


    /// Item Handlers

    private LazyOptional<IItemHandler> InputLazyItemHandler = LazyOptional.empty();
    private ItemStackHandler Inventory;
    public LazyOptional<IItemHandler> getInputLazyItemHandler(){return InputLazyItemHandler;}

    public IItemHandler getInputItems() {
        return Inventory;
    }
    @Override
    public void onLoad() {
        super.onLoad();
        InputLazyItemHandler = LazyOptional.of(() -> Inventory);
        SteamLazyFluidHandler = LazyOptional.of(() -> SteamTank.getPrimaryHandler());
        OxygenLazyFluidHandler = LazyOptional.of(() -> OxygenTank.getPrimaryHandler());
        MainTankFluidHandler = LazyOptional.of(() -> MainTank);
    }

    /// Capabilities!

    @Override
    public void invalidate() {
        super.invalidate();
        InputLazyItemHandler.invalidate();
        SteamLazyFluidHandler.invalidate();
        OxygenLazyFluidHandler.invalidate();
        MainTankFluidHandler.invalidate();
    }

    @Override
    public void initialize() {
        super.initialize();
        updateMultiblock(true);
    }

    ///Processing
    //Every tick we should do normal processing stuff, the handlers in here should be able to do all that behavoir
    public BlastFurnaceStatus BFData = BlastFurnaceStatus.empty();



    public int tickTimer = 1;

    //0 = no recipe, 1 = melting, 2 = alloying.
    public int recipeStatus = 0;
    public int recipeTimer = 0;
    public int recipeThreshold = -1;


    
    public int currentSize = 0;

    @Override
    public void tick() {
        super.tick();
        MainTank.tick();
        /// Update Multiblock & operate
        if (tickTimer-- <= 0){
            updateMultiblock(false);
            syncBFData();
            tickTimer = 20;

            //We should do nothing if we failed, and sadly cancel the recipe
            if (!lastAssemblyResult.success()){
                Overheated.LOGGER.info("Canceling Recipe");
                cancelRecipe();
                return;
            }

            //Start new recipe if you can
            if (recipeStatus == 0) {
                startNewRecipe();
            } else if (recipeStatus > 0){
                //A recipe is active
                recipeTimer += (BFData.PressureLevel + BFData.steamHeat);
                if (recipeTimer >= recipeThreshold){
                    finishRecipe();
                }
            }

            //See if we need to outgas
            if (!GasQueue.isEmpty()){
                releaseGasses();
            }
        }
    }

    public ResourceLocation currentRecipe = null;
    public void startNewRecipe(){
        Overheated.LOGGER.info("Trying to start Recipe");
        for (IndustrialMeltingRecipe r : level.getRecipeManager().getAllRecipesFor(IndustrialMeltingRecipe.Type.INSTANCE)){
            if(r.testRecipe(this,true)){
                currentRecipe = r.getId();
                recipeThreshold = r.getDuration();
                recipeStatus = 1;
                Overheated.LOGGER.info("Starting Melting Recipe");
                return;
            }
        }
        for (IndustrialBlastingRecipe r : level.getRecipeManager().getAllRecipesFor(IndustrialBlastingRecipe.Type.INSTANCE)){
            if(r.testRecipe(this,true)){
                currentRecipe = r.getId();
                recipeThreshold = r.getDuration();
                recipeStatus = 2;
                Overheated.LOGGER.info("Starting Blasting Recipe");
                return;
            }
        }
    }

    //Double check requirements
    //Complete the recipe if you can
    public void finishRecipe(){
        if (currentRecipe == null || recipeStatus == 0)
            return;
        level.getRecipeManager().byKey(currentRecipe).ifPresent(
            (recipeStatus == 1)
                ? (
                recipe -> ((IndustrialMeltingRecipe) recipe).testRecipe(this,false)
                )
                : (
                recipe -> ((IndustrialBlastingRecipe) recipe).testRecipe(this,false)
                )
        );
        cancelRecipe();
    }

    //Completely cancel the recipe
    public void cancelRecipe(){
        recipeThreshold = -1;
        recipeTimer = 0;
        recipeStatus = 0;
        currentRecipe = null;
    }

    ArrayList<FluidStack> GasQueue = new ArrayList<>();
    public void createGas(FluidStack gas){
        GasQueue.add(gas);
    }

    //TODO test gas explosions and such
    public void releaseGasses(){
        Iterator<BlockPos> emptyGasOutputs = MBData.getOutGasPositions(level);
        if (emptyGasOutputs.hasNext()) {
            for (FluidStack fs : GasQueue) {
                if (!emptyGasOutputs.hasNext()) {
                    return;
                } else if (fs.getAmount() >= 1000) {
                    placeGasBlock(emptyGasOutputs.next(), fs, level);
                    fs.shrink(1000);
                } else {
                    fs.shrink(1);
                }
            }
        } else {
            //This john might explode
            int explosionDamage = 0;
            for (FluidStack fs : GasQueue) {
                if (fs.getAmount() >= 1000) {
                    explosionDamage++;
                }
            }
            if (explosionDamage > 0){
                //This john will explode! Loop over this again until all gasses have been released
                causeExplosion(explosionDamage);
                releaseGasses();
            }
        }
    }

    public void causeExplosion(int power){
        Vec3 pos = MBData.bounds.getCenter();
        level.explode(null,pos.x,pos.y,pos.z,power, Level.ExplosionInteraction.TNT);
    }

    public void syncBFData() {
        FluidStack steam = SteamTank.getPrimaryHandler().getFluid();
        BFData.SteamAmount = steam.getAmount();
        BFData.PressureLevel = AllSteamFluids.getSteamPressure(steam);
        BFData.steamHeat = AllSteamFluids.getSteamHeat(steam);
        BFData.OxygenAmount = OxygenTank.getPrimaryHandler().getFluidAmount();

    }

    @Override
    public void remove() {
        super.remove();
        if (MBData != null) {
            for (BlockPos bp : MBData.servantPositions) {
                if (level.getBlockEntity(bp) instanceof BlastFurnaceServantBlockEntity bfsbe){
                    bfsbe.updateController(null);
                }
            }
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (lastAssemblyResult.error()){
            tooltip.add(addIndent(Component.translatable("coverheated.ibf.invalid_multiblock")));
            tooltip.add(addIndent(Component.translatable(lastAssemblyResult.message())));
            if (lastAssemblyResult.errorPos() != null) {
                tooltip.add(addIndent(Component.literal(lastAssemblyResult.errorPos().toString())));
                Outliner.getInstance().showAABB(this, new AABB(lastAssemblyResult.errorPos()), 1000);

            }
        } else {
            tooltip.add(addIndent(Component.translatable("coverheated.ibf.steam_tanks").withStyle(s -> s.withColor(ChatFormatting.AQUA).withBold(true))));
            containedFluidTooltip(tooltip,isPlayerSneaking,SteamLazyFluidHandler);
            containedFluidTooltip(tooltip,isPlayerSneaking,OxygenLazyFluidHandler);
            tooltip.add(addIndent(Component.translatable("coverheated.ibf.main_tank").withStyle(s->s.withColor(ChatFormatting.GOLD).withBold(true))));
            MainTank.addToGoggleTooltip(tooltip);
            tooltip.add(addIndent(Component.translatable("coverheated.ibf.gas_queue").withStyle(s->s.withColor(ChatFormatting.GRAY).withBold(true))));
            GoggleHelper.containedFluidArrayTooltip(tooltip, GasQueue, 0);


        }
        return true;

    }

    /*
        tickTimer
        meltingTimer
        meltingThreshold
        AlloyingTimer
        AlloyingThreshold
        CurrentSize

        BlastFurnaceStatus
        BlastFurnaceTank

        ItemHandler

        MultiBlockData
        LastAssemblyResult

        CurrentRecipe

        GasQueue

         */
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("ticktimer");
        recipeTimer = tag.getInt("recipetimer");
        recipeThreshold = tag.getInt("recipethreshold");
        recipeStatus = tag.getInt("recipestatus");
        currentSize = tag.getInt("currentsize");


        BFData = BlastFurnaceStatus.readTag(tag,"bfstatus");

        tag.put("inputItems", Inventory.serializeNBT());

        if (!tag.getBoolean("mbdatapresent")){
            MBData = null;
        }
        if (MBData != null) {
            MBData = new MultiblockData(tag, "multiblockdata", this);
        }

        lastAssemblyResult = MultiblockResult.Read(tag,"assemblystatus");

        currentRecipe = !tag.getString("recipe").equals("null") ? ResourceLocation.tryParse(tag.getString("recipe")) : null;

        GasQueue.clear();
        ListTag gasses = tag.getList("gasqueue", Tag.TAG_COMPOUND);
        for (int i = 0; i < gasses.size(); i++) {
            FluidStack toAdd = FluidStack.loadFluidStackFromNBT(gasses.getCompound(i));
            if (toAdd.isEmpty()) {continue;}
            GasQueue.add(toAdd);
        }

        if (!clientPacket) {
            Inventory.deserializeNBT(tag.getCompound("Inventory"));
        }


    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("ticktimer",tickTimer);
        tag.putInt("recipetimer",recipeTimer);
        tag.putInt("recipethreshold",recipeThreshold);
        tag.putInt("recipestatus",recipeStatus);
        tag.putInt("currentsize",currentSize);

        BFData.writeTag(tag, "bfstatus");

        Inventory.deserializeNBT(tag.getCompound("inputItems"));


        if (MBData != null) {
            MBData.writeTag(tag, "multiblockdata");
            tag.putBoolean("mbdatapresent",true);
        } else {
            tag.putBoolean("mbdatapresent",false);
        }

        lastAssemblyResult.write(tag,"assemblystatus");

        tag.putString("recipe",currentRecipe != null ? currentRecipe.getPath() : "null");

        ListTag list = new ListTag();
        for (FluidStack liquid : GasQueue) {
            CompoundTag fluidTag = new CompoundTag();
            liquid.writeToNBT(fluidTag);
            list.add(fluidTag);
        }
        tag.put("gasqueue", list);

        if (!clientPacket) {
            tag.put("Inventory", Inventory.serializeNBT());
        }

    }
}
