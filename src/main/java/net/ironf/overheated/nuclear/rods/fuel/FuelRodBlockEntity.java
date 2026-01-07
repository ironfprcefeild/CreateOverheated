package net.ironf.overheated.nuclear.rods.fuel;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.IGasPlacer;
import net.ironf.overheated.nuclear.radiation.RadiationMap;
import net.ironf.overheated.nuclear.rods.ControlRodsRegister;
import net.ironf.overheated.nuclear.rods.IMakeNeutrinos;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class FuelRodBlockEntity extends SmartBlockEntity implements
        IMakeNeutrinos, ControlRodsRegister.IControlRod, IGasPlacer, IHaveGoggleInformation {
    public FuelRodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    int tickTimer = 20;
    int dischargeTimer = 4;

    int decay = 0;
    int Heat = 0;
    int neutrinos = 1;

    int directionChecked = 0;

    //TODO make fuel rods turn into decayed ones
    //TODO make fuel rods retain their decay when mined
    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- <= 0){
            tickTimer = 80;

            //Fire Neutrinos (every 4 seconds)
            if (neutrinos > 0){
                fireNeutrinosOrthogonally(neutrinos,getBlockPos(),level);
                neutrinos = 0;
            } else {
                Heat = Math.max(Heat-1,0);
            }

            //Make Steam or explode (every 16 seconds)
            if (dischargeTimer-- <= 0){
                dischargeTimer = 4;

                if (Heat > 64) {
                    //Let's blow tf up >:)
                    causeMeltdown(Heat,getBlockPos());
                } else if (Heat > 0) {
                    decay++;
                    //Let's emit some steam :D
                    //Steam Values: 1-8 = unheated low, 9-16 = heated low, 17-24 = superheated mid, 25-32 = overheated mid, 33-40 = 2x Overheated mid, 41-48 = x3 Overheated High, 49-64 = x4 Overheated High
                    int steamPressure = Math.floorDiv(Heat, 16) + 1;
                    int steamHeating = Math.min(3, Math.floorDiv(Heat, 8));
                    int steamCount = Heat > 32
                            ? (Heat > 40
                                ? (Heat > 48 ? 4 : 3)
                                : 2)
                            : 1;
                    FluidStack steamCreated = AllSteamFluids.getSteamFromValues(steamPressure, steamHeating, 1);

                    directionChecked = (directionChecked+1)%4;
                    while (true){
                        BlockPos checked = getBlockPos().relative(Iterate.horizontalDirections[directionChecked]);
                        if (level.getFluidState(checked) == (AllSteamFluids.DISTILLED_WATER.SOURCE.get().getSource(false))){
                            steamCount--;
                            placeGasBlock(checked,steamCreated,level);
                            if (steamCount == 0){
                                Heat = 0;
                                break;
                            }
                        }
                        directionChecked++;
                        if (directionChecked >= 4){
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public Integer regulate(int incomingNeutrinos, Direction direction, BlockPos pos, BlockState state, Level level) {
        neutrinos += incomingNeutrinos;
        Heat += incomingNeutrinos;
        propagateNeutrino(incomingNeutrinos,Direction.UP,1);
        propagateNeutrino(incomingNeutrinos,Direction.DOWN,1);
        return 0;
    }

    public void propagateNeutrino(int amount, Direction d, int offset){
        if (level.getBlockEntity(getBlockPos().relative(d,offset)) instanceof FuelRodBlockEntity FRBE){
            //Spread impact to the above fuel rod
            FRBE.neutrinos += amount;
            FRBE.Heat += amount;
            FRBE.propagateNeutrino(amount,d,1);
        } else if (level.getBlockState(getBlockPos().relative(d,offset)).is(AllBlocks.NUCLEAR_CASING.get())){
            //No fuel rod, but casing, so we call this again with more offset (until we find a rod or other)
            propagateNeutrino(amount,d,offset+1);
        }
    }


    public void causeMeltdown(int Heat, BlockPos pos){
        fireNeutrinosOrthogonally(Heat,pos,level);
        //Explosions seem to be desynced on client/server
        Overheated.LOGGER.info("Blowing up on client?" + level.isClientSide);
        RadiationMap.pulseRadiation(level, pos,5);
        //level.explode(null,pos.getX(),pos.getY(),pos.getZ(),Math.min(Heat/2,64),Level.ExplosionInteraction.BLOCK);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        Heat = tag.getInt("heat");
        neutrinos = tag.getInt("neutrinos");
        tickTimer = tag.getInt("timer");
        dischargeTimer = tag.getInt("dischargetimer");
        directionChecked = tag.getInt("directionChecked");
        decay = tag.getInt("decay");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("heat",Heat);
        tag.putInt("neutrinos",neutrinos);
        tag.putInt("timer",tickTimer);
        tag.putInt("dischargetimer",dischargeTimer);
        tag.putInt("directionChecked",directionChecked);
        tag.putInt("decay",decay);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(addIndent(Component.literal("Heat:" + Heat)));
        tooltip.add(addIndent(Component.literal("Neu:" + neutrinos)));
        tooltip.add(addIndent(Component.literal("Decay:" + decay)));

        return true;
    }
}
