package net.ironf.overheated.steamworks.blocks.turbine.multiblock;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.steamworks.blocks.turbine.turbineShaft.turbineShaftBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineVent.turbineVentBlock;
import net.ironf.overheated.util.OLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class turbineBlockEntity extends SmartBlockEntity implements IMultiBlockEntityContainer, IHaveGoggleInformation {

    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected int radius;
    protected int length;
    protected Direction.Axis axis;
    public int currentPressure;
    public int mbSteamIn;
    public int mbSteamOut;
    public int flowThroughRate;
    public Boolean isDirectionInverted;


    public static final Logger LOGGER = LogUtils.getLogger();

    public turbineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        updateConnectivity = false;
        radius = 1;
        length = 1;
        currentPressure = 0;
        mbSteamIn = 0;
        mbSteamOut = 0;
        flowThroughRate = 0;
        isDirectionInverted = true;
        updateFlowThroughRate();
    }


    //Doing Stuff
    @Override
    public void tick() {
        super.tick();


        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateConnectivity)
            updateConnectivity();

    }

    public void updateFlowDirection(){
        updateFlowDirection(Optional.empty(),null);
    }

    //isVent param is nullable only if attachement pos is an empty optional
    public void updateFlowDirection(Optional<BlockPos> AttachmentPos, Boolean isVent) {


        //Find if connection is already ont he proper axis (like this table below shows), only done if a new attachment was added, otherwise assume the current axis.
        /*
              V   S
            I -1  1
            N 1  -1
        */

        if (AttachmentPos.isPresent()) {
            BlockPos newAttachmentPos = AttachmentPos.get();
            int relativeDistance;
            if ((isVent && isDirectionInverted) || (!isVent && !isDirectionInverted)) {
                relativeDistance = -1;
            } else {
                relativeDistance = 1;
            }

            //This will invert the direction axis if the new block changes how its aligned
            if (!(isConnectedTurbine(newAttachmentPos.relative(axis, relativeDistance)))) {
                isDirectionInverted = !isDirectionInverted;
            }
        }

        boolean newAttachment = (isVent != null);

        //Get controller, use this if contorller is null
        turbineBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null){
            controllerBE = this;
        }

        //Next we check all positions for other edge attachments, and remove them if they are the same type of attachement or no longer align with the axis
        for (int yOffset = 0; yOffset < controllerBE.getHeight(); yOffset++) {
            for (int xOffset = 0; xOffset < controllerBE.getWidth(); xOffset++) {
                for (int zOffset = 0; zOffset < controllerBE.getWidth(); zOffset++) {
                    BlockPos pos = controllerBE.getBlockPos().offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!turbineBlock.isTurbine(blockState))
                        continue;
                    if (AttachmentPos.isPresent() && pos == AttachmentPos.get())
                        continue;

                    //See what side we are on, or if we are not and edge
                    int inAxisTurbines = 0;
                    Boolean ventSide = null;
                    Optional<turbineBlockEntity> checkedBlock = turbineBlock.getTurbineHelper(this.level,pos.relative(axis,1),false);
                    if (checkedBlock.isPresent() && checkedBlock.get().getControllerBE() != null && checkedBlock.get().getControllerBE().getBlockPos() == controllerBE.getBlockPos()) {
                        inAxisTurbines++;
                        ventSide = true;
                    }
                    Optional<turbineBlockEntity> otherCheckedBlock = turbineBlock.getTurbineHelper(this.level,pos.relative(axis,-1),false);
                    if (otherCheckedBlock.isPresent() && otherCheckedBlock.get().getControllerBE() != null && otherCheckedBlock.get().getControllerBE().getBlockPos() == controllerBE.getBlockPos()) {
                        inAxisTurbines++;
                        ventSide = false;
                    }
                    //TODO Both of theese are always being triggered regardless of chosen turbine. This makes edge detection not work int he same the IsEdge Method isnt
                    if (ventSide == null){
                        return;
                    }

                    //set a bool if were on the edge, indicated to remove all he things around it
                    boolean edge = (controllerBE.length == 1 || controllerBE.length == 2 || inAxisTurbines != 2);

                    //Iterate through attached blocks of the  turbine
                    for (Direction d : Iterate.directions) {
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        //Remove if matches added block (or assume the first block found is the added), or if doesn't match the ventSide boolean, or if not on edge
                        if (AllBlocks.TURBINE_SHAFT.has(attachedState) && turbineShaftBlock.getAttachedDirection(attachedState).getOpposite() == d){
                            //IF not on the shaft side or if not on the egde
                            if (ventSide || !edge){
                                LOGGER.info("Removing at Point A");
                                LOGGER.info("Recognized " + inAxisTurbines + " In Axis Turbines");
                                LOGGER.info("Recognized a length of " + controllerBE.length);
                                LOGGER.info("Edge Is: " + edge);
                                LOGGER.info("Vent Side Is: " + ventSide);
                                level.removeBlock(attachedPos,true);
                                continue;
                            } else if (newAttachment) {
                                //IF is shaft and not the new attachment
                                if (!isVent && !(AttachmentPos.isPresent() && AttachmentPos.get() == attachedPos)) {
                                    LOGGER.info("Removing at Point B");
                                    LOGGER.info("Recognized " + inAxisTurbines + " In Axis Turbines");
                                    LOGGER.info("Recognized a length of " + controllerBE.length);
                                    LOGGER.info("Edge Is: " + edge);
                                    LOGGER.info("Vent Side Is: " + ventSide);
                                    level.removeBlock(attachedPos, true);
                                    continue;
                                }
                            } else {
                                newAttachment = true;
                                isVent = false;
                            }
                        }
                        if (AllBlocks.TURBINE_VENT.has(attachedState) && turbineVentBlock.getAttachedDirection(attachedState).getOpposite() == d){
                            //IF not on the edge or not on the vent side
                            if (!ventSide || !edge){
                                LOGGER.info("Removing at Point C");
                                LOGGER.info("Recognized " + inAxisTurbines + " In Axis Turbines");
                                LOGGER.info("Recognized a length of " + controllerBE.length);
                                LOGGER.info("Edge Is: " + edge);
                                LOGGER.info("Vent Side Is: " + ventSide);
                                level.removeBlock(attachedPos,true);
                            } else if (newAttachment) {
                                //IF is vent and not the new attachment
                                if (isVent && !(AttachmentPos.isPresent() && AttachmentPos.get() != attachedPos)) {
                                    LOGGER.info("Removing at Point D");
                                    LOGGER.info("Recognized " + inAxisTurbines + " In Axis Turbines");
                                    LOGGER.info("Recognized a length of " + controllerBE.length);
                                    LOGGER.info("Edge Is: " + edge);
                                    LOGGER.info("Vent Side Is: " + ventSide);
                                    level.removeBlock(attachedPos, true);
                                }
                            } else {
                                newAttachment = true;
                                isVent = true;
                            }
                        }
                    }
                }
            }
        }
        updateFlowThroughRate();
    }
    public boolean isConnectedTurbine(BlockPos checkAt){
        Optional<turbineBlockEntity> checkedBlock = turbineBlock.getTurbineHelper(this.level,checkAt,false);
        return (checkedBlock.isPresent() && checkedBlock.get().getControllerBE().getBlockPos() == this.getControllerBE().getBlockPos());
    }

    //This should only be called on the controller
    public int getCapacities() {
        return 10 * ((this.radius + 4) * this.length);
    }

    //Multiblock
    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide())
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
        //updateFlowDirection();
    }

    private void updateFlowThroughRate() {
        flowThroughRate = (int) ((length * radius) * 0.1);
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    //Copious Overrides
    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @SuppressWarnings("unchecked")
    @Override
    public turbineBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof turbineBlockEntity)
            return (turbineBlockEntity) blockEntity;
        return null;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void setController(BlockPos pos) {
        if (level.isClientSide && !isVirtual())
            return;
        if (pos.equals(this.controller))
            return;
        this.controller = pos;
        updateFlowThroughRate();
        setChanged();
        sendData();

    }

    @Override
    public void removeController(boolean keepValues) {
        if (level.isClientSide())
            return;
        updateConnectivity = true;
        controller = null;
        radius = 1;
        length = 1;

        BlockState state = getBlockState();
        if (turbineBlock.isTurbine(state)) {
            state = state.setValue(turbineBlock.LARGE, false);
            getLevel().setBlock(worldPosition, state, 22);
        }

        //updateFlowDirection();
        setChanged();
        sendData();

    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (turbineBlock.isTurbine(state)) { // safety
            level.setBlock(getBlockPos(), state.setValue(turbineBlock.LARGE, radius > 2), 6);
        }

        //updateFlowDirection();
        setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return getMainAxisOf(this);
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y) return getMaxWidth();
        return getMaxLength(width);
    }

    public static int getMaxLength(int radius) {
        return radius * 3;
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return length;
    }

    @Override
    public int getWidth() {
        return radius;
    }

    @Override
    public void setHeight(int height) {
        this.length = height;
    }

    @Override
    public void setWidth(int width) {
        this.radius = width;
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


    //Read/Writes

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = radius;
        int prevLength = length;

        updateConnectivity = compound.contains("Uninitialized");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            radius = compound.getInt("Size");
            length = compound.getInt("Length");
            mbSteamIn = compound.getInt("SteamIn");
            mbSteamOut = compound.getInt("SteamOut");
            flowThroughRate = compound.getInt("SteamIn");
            isDirectionInverted = compound.getBoolean("isInverted");


            //The FTR is determinable form the length and radius, so there is no need to save it.
            updateFlowDirection();
        }

        boolean changeOfController = controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (hasLevel() && (changeOfController || prevSize != radius || prevLength != length))
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putInt("Size", radius);
            compound.putInt("Length", length);
            compound.putInt("SteamIn", mbSteamIn);
            compound.putInt("SteamOut", mbSteamOut);
            compound.putBoolean("isInverted", isDirectionInverted);
        }

        super.write(compound, clientPacket);
    }

    //Edge checker to help with turbine vents and shafts placement
    public boolean isOnEdge() {
        turbineBlockEntity controllerBE = this.getControllerBE();

        //If the turbine is only 1 or 2 long, all turbine blocks are edges, otherwise more logic
        if (controllerBE.length == 1 || controllerBE.length == 2) {
            return true;
        }
        int aroundMe = 0;
        Optional<turbineBlockEntity> checkedBlock = turbineBlock.getTurbineHelper(this.level,smartRelative(axis, Direction.AxisDirection.POSITIVE,this.getBlockPos()),false);
        OLogger.LogPos(smartRelative(axis, Direction.AxisDirection.POSITIVE,this.getBlockPos()),LOGGER);
        if (checkedBlock.isPresent() && checkedBlock.get().getControllerBE().getBlockPos() == controllerBE.getBlockPos()) {
            aroundMe++;
        }
        Optional<turbineBlockEntity> otherCheckedBlock = turbineBlock.getTurbineHelper(this.level,smartRelative(axis, Direction.AxisDirection.NEGATIVE,this.getBlockPos()),false);
        OLogger.LogPos(smartRelative(axis, Direction.AxisDirection.NEGATIVE,this.getBlockPos()),LOGGER);
        if (otherCheckedBlock.isPresent() && otherCheckedBlock.get().getControllerBE().getBlockPos() == controllerBE.getBlockPos()) {
            aroundMe++;
        }
        return (aroundMe == 1 || aroundMe == 0);
    }

    public BlockPos smartRelative(Direction.Axis axis, Direction.AxisDirection direction, BlockPos from){
        int step = direction.getStep();
        int Xmask = axis.choose(1,0,0) * step;
        int Ymask = axis.choose(0,1,0) * step;
        int Zmask = axis.choose(0,0,1) * step;
        return new BlockPos(from.getX() + Xmask, from.getY() * Ymask, from.getZ() * Zmask);
    }

    //TODO smart realtive isnt directly working, edges are recognized as edges but so are middle blocks


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal(Component.translatable("overheated.turbine.in").getString() + getControllerBE().mbSteamIn + Component.translatable("overheated.turbine.out").getString() + getControllerBE().mbSteamOut));
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}

