package net.ironf.overheated.nuclear.rods;

import com.mojang.datafixers.TypeRewriteRule;
import com.simibubi.create.api.registry.SimpleRegistry;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ControlRodsRegister {

    public interface IControlRod {
        Integer regulate(int incomingNeutrinos, Direction direction, BlockPos pos, BlockState state, Level level);

    }

    private static final SimpleRegistry<Block, IControlRod> REGULATORS = SimpleRegistry.create();

    public static void registerRegulator(Block block, IControlRod controlrod) {
        REGULATORS.register(block,controlrod);
    }

    public static void registerBERegulator(Block block){
        registerRegulator(block, (amount,dir,pos,state,level) -> {
            IControlRod be = ((IControlRod)(level.getBlockEntity(pos)));
            return be.regulate(amount,dir,pos,state,level);
        });
    }
    public static void registerSimpleRegulator(Block block, int alwaysReturn){
        registerRegulator(block, (n,d,pos,state,level) -> alwaysReturn);
    }
    public static Integer doRegulation(int incomingNeutrinos, Direction incoming, Level level, BlockPos pos, BlockState state) {
        IControlRod rod = REGULATORS.get(state.getBlock());
        return rod != null ? rod.regulate(incomingNeutrinos,incoming,pos,state,level) : incomingNeutrinos;
    }

    public static void registerDefaults(){
        Overheated.LOGGER.info("O: Registering Default Control and Fuel Rods");
        registerBERegulator(AllBlocks.URANIUM_FUEL_ROD.get());
        registerBERegulator(AllBlocks.CONTROL_ROD.get());
        registerBERegulator(AllBlocks.RADIOLYZER.get());
        registerSimpleRegulator(AllBlocks.NUCLEAR_CASING.get(),0);
    }

}
