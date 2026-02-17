package net.ironf.overheated.steamworks.blocks.turbine.turbineFan;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.simibubi.create.content.kinetics.base.KineticBlockEntity.convertToDirection;
import static net.createmod.catnip.data.Iterate.directions;

public class turbineFanBlockEntity extends SmartBlockEntity implements IAirCurrentSource, IHaveGoggleInformation {
    public turbineFanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        airCurrent = new AirCurrent(this);
        updateAirFlow = true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public AirCurrent airCurrent;
    protected int airCurrentUpdateCooldown;
    protected int entitySearchCooldown;
    protected boolean updateAirFlow;

    public int lastTurbineSpin = 0;
    public boolean shouldReverseSpin = false;
    public int turbineTimer = 10;
    public int lastTurbineRadius = 0;

    public boolean centerBlock = true;
    /// Doing Stuff
    @Override
    public float getSpeed() {
        return lastTurbineSpin * (shouldReverseSpin ? -1 : 1);
    }

    @Override
    public void tick() {
        super.tick();

        //Handle current
        handleCurrent();

        //Manage Turbine
        if (turbineTimer > 0){
            turbineTimer--;
            if (turbineTimer <= 0){
                lastTurbineSpin = 0;
                updateAirFlow = true;
            }
        }

    }

    public void updateFromTurbine(int spinDrain, int spinRadius, boolean reverseSpin, boolean propagate){
        lastTurbineSpin = spinDrain;
        shouldReverseSpin = reverseSpin;
        lastTurbineRadius = spinRadius;
        turbineTimer = 320;
        updateAirFlow = true;

        // Search for other fan blocks and begin them spinning
        if (propagate){
            centerBlock = true;
            BlockPos bp;
            ArrayList<BlockPos> toActivate = new ArrayList<>();
            Set<BlockPos> secondPass = new LinkedHashSet<>();
            //Find blockpos of cardinally adjacent blocks
            for (Direction d : directions) {
                bp = getBlockPos().relative(d);
                if (AllBlocks.TURBINE_FAN.has(level.getBlockState(bp))) {
                    for (Direction d2 : directions){
                        if (d2 == d.getOpposite()){
                            continue;
                        }
                        secondPass.add(bp.relative(d2));
                    }
                    toActivate.add(bp);
                }
            }
            //The rest of the possible locations
            for (BlockPos bp2 : secondPass){
                if (AllBlocks.TURBINE_FAN.has(level.getBlockState(bp2))) {
                    toActivate.add(bp2);
                }
            }
            //Turn them all on if it can work
            if (toActivate.size() <= spinRadius){
                for (BlockPos fanPos : toActivate){
                    ((turbineFanBlockEntity) level.getBlockEntity(fanPos))
                    .updateFromTurbine(spinDrain,spinRadius,reverseSpin,false);
                }
            }
        } else {
            centerBlock = false;
        }
    }

    public void handleCurrent(){
        boolean server = !level.isClientSide || isVirtual();

        if (server && airCurrentUpdateCooldown-- <= 0) {
            airCurrentUpdateCooldown = AllConfigs.server().kinetics.fanBlockCheckRate.get();
            updateAirFlow = true;
        }

        if (updateAirFlow) {
            updateAirFlow = false;
            airCurrent.rebuild();
            sendData();
        }

        if (getSpeed() == 0)
            return;

        if (entitySearchCooldown-- <= 0) {
            entitySearchCooldown = 5;
            airCurrent.findEntities();
        }

        airCurrent.tick();
    }

    /// Read/Write
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (clientPacket)
            airCurrent.rebuild();
        lastTurbineSpin = tag.getInt("last_spin");
        lastTurbineRadius = tag.getInt("last_radius");
        shouldReverseSpin = tag.getBoolean("reverse");
        centerBlock = tag.getBoolean("center");
        turbineTimer = tag.getInt("turbine_timer");
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("last_spin",lastTurbineSpin);
        tag.putInt("last_radius",lastTurbineRadius);
        tag.putBoolean("reverse",shouldReverseSpin);
        tag.putBoolean("center",centerBlock);
        tag.putInt("turbine_timer",turbineTimer);
    }
    /// Goggles
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.airflow").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(((float) lastTurbineSpin) /256)).withStyle(ChatFormatting.AQUA),1));
        return true;
    }

    /// Air Current Architecture
    @Override
    public @Nullable AirCurrent getAirCurrent() {
        return airCurrent;
    }

    @Override
    public @Nullable Level getAirCurrentWorld() {
        return level;
    }

    @Override
    public BlockPos getAirCurrentPos() {
        return worldPosition;
    }

    @Override
    public Direction getAirflowOriginSide() {
        return this.getBlockState()
                .getValue(turbineFanBlock.FACING);
    }

    @Override
    public @Nullable Direction getAirFlowDirection() {
        float speed = getSpeed();
        if (speed == 0)
            return null;
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        speed = convertToDirection(speed, facing);
        return speed > 0 ? facing : facing.getOpposite();
    }

    @Override
    public boolean isSourceRemoved() {
        return remove;
    }

    public void blockInFrontChanged() {
        updateAirFlow = true;
    }

}
