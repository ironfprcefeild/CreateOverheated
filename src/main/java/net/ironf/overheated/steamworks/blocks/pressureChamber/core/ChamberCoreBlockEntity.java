package net.ironf.overheated.steamworks.blocks.pressureChamber.core;

import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllTags;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionType;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.IChamberAdditionBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.item.ChamberItemBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.item.ChamberItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChamberCoreBlockEntity extends SmartBlockEntity {
    public ChamberCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public int tickTimer = 10;
    public boolean crafting;
    public float currentHeat = 0;
    public boolean valid = false;
    public ArrayList<BlockPos> steamIns = new ArrayList<>();
    public ArrayList<BlockPos> steamOuts = new ArrayList<>();
    public ArrayList<BlockPos> Item = new ArrayList<>();
    public ArrayList<BlockPos> HeatSinkers = new ArrayList<>();
    public ArrayList<BlockPos> LaserAbsorbers = new ArrayList<>();

    public ResourceLocation currentRecipe = null;
    @Override
    public void tick() {
        if (tickTimer <= 0){
            //This can be arg false because all additions call it with true when they are added or removed
            updateAdditions(false);
            //If update additions is all good we can try to craft
            //This Bool also includes information about if we have the minimum number of each addition to operate.
            if (valid) {
                //If we are currently crafting, we can finish the craft.
                //The tickTimer will be set to the current recipes time taken when start crafting is called
                if (crafting) {
                    finishCraft();
                    crafting = false;
                    tickTimer = 10;
                } else {
                    //Everything looks good, so try to start crafting
                    startCraft();
                }
            }
        } else {
            tickTimer--;
        }
        if (currentHeat > 0){
            currentHeat = (float) Math.max(currentHeat - findHeatSunken() * 0.1,0);
        }
        handleSteam();
        handleHeat();
    }

    public void handleHeat() {
    }

    public void handleSteam() {

    }
    public void updateAdditions(boolean updateList) {
        //Updates the lists of additions to this chamber
        //Also checks for walling
        //Returns true if valid chamber
        if (updateList) {
            steamIns.clear();
            steamOuts.clear();
            Item.clear();
            HeatSinkers.clear();
            LaserAbsorbers.clear();
        }
        for (int x = -1; x < 2; x++){
            for (int y = -1; y < 2; y++){
                for (int z = -1; z < 2; z++) {
                    BlockPos lookAt = getBlockPos().offset(x,y,z);
                    //We are the face of a block. so we need to check for an addition, we dont need to do any of this if we aren't updating the list
                    if (updateList && Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
                        switch (getTypeAt(lookAt, level)) {
                            case STEAM_IN -> steamIns.add(lookAt);
                            case STEAM_OUT -> steamOuts.add(lookAt);
                            case ITEM -> Item.add(lookAt);
                            case HEAT_SINKER -> HeatSinkers.add(lookAt);
                            case LASER_ABSORBER -> LaserAbsorbers.add(lookAt);
                            case INVALID -> {
                                if (!AllTags.AllBlockTags.CHAMBER_BORDER.matches(level.getBlockState(lookAt))) {
                                    valid = false;
                                    return;
                                }
                            }
                        }
                    } else {
                        valid = (lookAt != getBlockPos() && !AllTags.AllBlockTags.CHAMBER_BORDER.matches(level.getBlockState(lookAt)));
                        return;
                    }
                }
            }
        }
        valid = hasMinimumAdditions();
    }
    public boolean hasMinimumAdditions(){
        return steamOuts.size() > 0 && steamIns.size() > 0 && Item.size() > 0;
    }



    public void startCraft() {
        //Sees if we can craft
        //Sets crafting boolean
        //sets Tick Timer to 10 if we couldn't craft, or the crafting delay if we could
        for (ItemVaultBlockEntity vault : getVaults()){
            Optional<PressureChamberRecipe> recipe = level.getRecipeManager().getRecipeFor(PressureChamberRecipe.Type.INSTANCE,convertHandler(vault.getInventoryOfBlock()),level);
            if (recipe.isPresent()){
                //We startin' a craft baby!
                //Because we have other conditions for recipes working
                if (getPressure() >= recipe.get().getSteamPressure() && getHeating() >= recipe.get().getLaserHeat()){
                    currentRecipe = recipe.get().getId();
                    tickTimer = recipe.get().getTicksTaken();
                    crafting = true;
                    return;
                }
            }
        }
        tickTimer = 10;
    }


    public SimpleContainer convertHandler(ItemStackHandler toConvert){
        SimpleContainer inventory = new SimpleContainer(toConvert.getSlots());
        for (int i = 0; i < toConvert.getSlots(); i++){
            inventory.setItem(i,toConvert.getStackInSlot(i));
        }
        return inventory;
    }


    public void finishCraft() {
        //Adds the proper items to the thing
        //Adds the proper heat increase
        Optional<PressureChamberRecipe> recipe = (Optional<PressureChamberRecipe>) level.getRecipeManager().byKey(currentRecipe);
        currentHeat = recipe.get().getHeatAdded();
        //TODO put items in vlauts

    }

    public List<ItemVaultBlockEntity> getVaults(boolean returnFirst){
        List<ItemVaultBlockEntity> toReturn = new ArrayList<>();
        for (BlockPos bp : Item){
            try {
                ItemVaultBlockEntity vault = ((ChamberItemBlockEntity) level.getBlockEntity(bp)).getVault();
                if (vault != null) {
                    toReturn.add(vault);
                    if (returnFirst){
                        break;
                    }
                }
            } catch (NullPointerException ignored){}
        }
        return toReturn;
    }
    public List<ItemVaultBlockEntity> getVaults(){
        return getVaults(false);
    }

    public float getHeating() {
    }

    public int getPressure() {
    }

    public float findHeatSunken() {
        //Addresses Additions and count heat Sinks
        //Applies some math to that number, and adds the baseline dissipation
        return 0f;
    }

    public static ChamberAdditionType getTypeAt(BlockPos at, Level level){
        BlockEntity Be = level.getBlockEntity(at);
        return (Be instanceof IChamberAdditionBlockEntity) ? ((IChamberAdditionBlockEntity) Be).getAdditionType() : ChamberAdditionType.INVALID;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("tick_timer");
        crafting = tag.getBoolean("crafting");
        steamIns = readBlockPosArrayList(tag,"steam_in");
        steamOuts = readBlockPosArrayList(tag,"steam_out");
        Item = readBlockPosArrayList(tag,"item");
        HeatSinkers = readBlockPosArrayList(tag,"heat_sinkers");
        LaserAbsorbers = readBlockPosArrayList(tag,"laser_absorbers");
        valid = tag.getBoolean("valid");
    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("tick_timer",tickTimer);
        tag.putBoolean("crafting",crafting);
        putBlockPosArrayList(tag,"steam_in",steamIns);
        putBlockPosArrayList(tag,"steam_out",steamOuts);
        putBlockPosArrayList(tag,"item", Item);
        putBlockPosArrayList(tag,"heat_sinkers",HeatSinkers);
        putBlockPosArrayList(tag,"laser_absorbers",LaserAbsorbers);
        tag.putBoolean("valid",valid);

    }

    public static void putBlockPosArrayList(CompoundTag tag,String at, ArrayList<BlockPos> list){
        tag.putInt(at + "_length",list.size());
        int i = -1;
        for (BlockPos pos : list){
            i++;
            tag.putLong(at + "_" + i,pos.asLong());
        }
    }

    public static ArrayList<BlockPos> readBlockPosArrayList(CompoundTag tag, String from){
        int length = tag.getInt(from + "_length");
        ArrayList<BlockPos> toReturn = new ArrayList<>(length);
        int i = -1;
        while (!(i > length)){
            i++;
            toReturn.add(BlockPos.of(tag.getLong(from + "_" + i)));
        }
        return toReturn;
    }

    //Goggles
    public void pullTooltip() {
    //TODO add tooltip support
    }
}
