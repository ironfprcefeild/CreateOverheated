package net.ironf.overheated.laserOptics.blazeCrucible;

import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.engine_room.flywheel.api.backend.BackendManager;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;


public class BlazeCrucibleBlockEntity extends SmartBlockEntity {

    public int timeHeated = 0;
    public int heatLevel = 0;
    public boolean needsStateUpdate = true;


    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            if (shouldTickAnimation())
                tickAnimation();
            if (!isVirtual())
                spawnParticles(heatLevel);
            return;
        }

        if (needsStateUpdate){
            updateBlockState();
            needsStateUpdate = false;
        }
        if (timeHeated > 0) {
            this.timeHeated--;
        } else {
            heatLevel = 0;
            needsStateUpdate = true;
        }
    }

    public void updateBlockState() {
        setBlockHeat(heatLevel);
    }

    public void setBlockHeat(int heat) {
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlazeBurnerBlock.HEAT_LEVEL,
                switch(heat){
                    case 3, 2 -> BlazeBurnerBlock.HeatLevel.SEETHING;
                    case 1 -> BlazeBurnerBlock.HeatLevel.KINDLED;
                    default ->  BlazeBurnerBlock.HeatLevel.SMOULDERING;
                }));
        notifyUpdate();
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock() {
        return BlazeBurnerBlock.getHeatLevelOf(getBlockState());
    }

    protected BlazeBurnerBlock.HeatLevel getHeatLevel() {
        BlazeBurnerBlock.HeatLevel level = BlazeBurnerBlock.HeatLevel.SMOULDERING;
        if (timeHeated > 0) {
            if (heatLevel > 1){
                //Superheated
                level = BlazeBurnerBlock.HeatLevel.SEETHING;
            } else {
                //Heated
                level = BlazeBurnerBlock.HeatLevel.KINDLED;
            }
        }
        return level;
    }

    public BlazeCrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        headAngle = LerpedFloat.angular();
        headAnimation = LerpedFloat.linear();
        headAngle.startWithValue((AngleHelper.horizontalAngle(state.getOptionalValue(BlazeBurnerBlock.FACING)
                .orElse(Direction.SOUTH)) + 180) % 360);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.timeHeated = tag.getInt("timeHeated");
        this.heatLevel = tag.getInt("heatLevel");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timeHeated",this.timeHeated);
        tag.putInt("heatLevel",this.heatLevel);
        needsStateUpdate = true;
    }

    public static void addToBoilerHeaters(){
        Overheated.LOGGER.info("O: Adding the Blaze Crucible to Boiler Heaters");
        BoilerHeater.REGISTRY.register(AllBlocks.BLAZE_CRUCIBLE.get(), (level, pos, state) -> {
            try {
                BlazeCrucibleBlockEntity crucible = ((BlazeCrucibleBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos)));
                int timeHeated = crucible.timeHeated;
                int heatLevel = crucible.heatLevel;
                if (timeHeated > 0) {
                    return heatLevel;
                }
                return 0;
            } catch (NullPointerException e){
                return -1;
            }
        });
    }

    @Override
    public void onLoad() {
        super.onLoad();
        needsStateUpdate = true;
    }

    //This code is straight from the blaze burner, changed to fit how this thingy works

    protected LerpedFloat headAnimation;
    protected LerpedFloat headAngle;


    @OnlyIn(Dist.CLIENT)
    private boolean shouldTickAnimation() {
        // Offload the animation tick to the visual when flywheel in enabled
        return !BackendManager.isBackendOn();
    }
    @OnlyIn(Dist.CLIENT)
    void tickAnimation() {

        float target = 0;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isInvisible()) {
            double x;
            double z;
            if (isVirtual()) {
                x = -4;
                z = -10;
            } else {
                x = player.getX();
                z = player.getZ();
            }
            double dx = x - (getBlockPos().getX() + 0.5);
            double dz = z - (getBlockPos().getZ() + 0.5);
            target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90;
        }
        target = headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), target);
        headAngle.chase(target, .25f, LerpedFloat.Chaser.exp(5));
        headAngle.tickChaser();
        headAnimation.chase(1, .25f, LerpedFloat.Chaser.exp(.25f));
        headAnimation.tickChaser();
    }

    protected void spawnParticles(int heatLevel) {
        if (level == null || heatLevel == 0)
            return;

        RandomSource r = level.getRandom();

        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
                .multiply(1, 0, 1));

        if (r.nextInt(4) != 0)
            return;

        boolean empty = level.getBlockState(worldPosition.above())
                .getCollisionShape(level, worldPosition.above())
                .isEmpty();

        if (empty || r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);

        double yMotion = empty ? .0625f : r.nextDouble() * .0125f;
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                        .multiply(1, .25f, 1)
                        .normalize()
                        .scale((empty ? .25f : .5) + r.nextDouble() * .125f))
                .add(0, .5, 0);

        if (heatLevel >= 2) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        } else if (heatLevel == 1) {
            level.addParticle(ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        } else {
            level.addParticle(ParticleTypes.SMOKE, v2.x, v2.y, v2.z, 0, yMotion, 0);
        }
    }

}
