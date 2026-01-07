package net.ironf.overheated.nuclear.rods.fuel;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FuelRodBlock extends Block implements IBE<FuelRodBlockEntity> {
    public FuelRodBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<FuelRodBlockEntity> getBlockEntityClass() {
        return FuelRodBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FuelRodBlockEntity> getBlockEntityType() {
        return AllBlockEntities.FUEL_ROD.get();
    }

    /*
    //Heat (Integer 0-63)
    public static final IntegerProperty HEAT = IntegerProperty.create("heat",0,63);
    //Qeued Nuetrinos (Integer 0-63)
    public static final IntegerProperty NEUTRINOS = IntegerProperty.create("neutrinos",0,63);
    public static final IntegerProperty COOLDOWN = IntegerProperty.create("cooldown",0,4);
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(HEAT).add(NEUTRINOS).add(COOLDOWN));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(HEAT,0).setValue(NEUTRINOS,1).setValue(COOLDOWN,4);
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(state, level, pos, p_60569_, p_60570_);
        level.scheduleTick(pos,this,20);
    }

    /*

      @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        super.tick(state,level,pos,rand);



        //Fire Neutrinos
        if (state.getValue(NEUTRINOS) > 0) {
          fireNeutrinosOrthogonally(state.getValue(NEUTRINOS), pos, level);
          state.setValue(NEUTRINOS, 0);
        }

        //Increment Cooldown
        state.setValue(COOLDOWN,state.getValue(COOLDOWN)-1);
        if (state.getValue(COOLDOWN) >= 0){
            state.setValue(COOLDOWN,4);
            //Make Steam or Explode
            //(0-7: Low Unheated, 8-15: Low Heated, 16-23: Mid Superheated, 24-31: Mid Overheated, 32-39: 2x Mid Overheated, 40-48: 3x High Overheated, 49+: Explodes!)
            int Heat = state.getValue(HEAT);
            if (Heat > 48) {
                //Lets blow tf up >:)
                causeMeltdown(Heat,pos,state,level);
            } else if (Heat > 0) {
                //Lets emit some steam :D
                int steamPressure = Math.floorDiv(Heat, 16) + 1;
                int steamHeating = Math.min(3, Math.floorDiv(Heat, 8));
                int steamCount = Heat >= 32 ? (Heat >= 40 ? 3 : 2) : 1;
                FluidStack steamCreated = AllSteamFluids.getSteamFromValues(steamPressure, steamHeating, 1);

                int firstSpotChecked = rand.nextIntBetweenInclusive(0,7);
                for (int i = firstSpotChecked; i != firstSpotChecked-1 ; i = (i+1)%7) {
                    BlockPos checked = getCardinalFromOrdinal(pos,firstSpotChecked);
                    if (level.getBlockState(checked).is(AllSteamFluids.DISTILLED_WATER.FLUID_BLOCK.get())){
                        steamCount--;
                        placeGasBlock(checked,steamCreated,level);
                        if (steamCount == 0){
                            state.setValue(HEAT,0);
                            break;
                        }
                    }
                }

            }

        }

        Overheated.LOGGER.info("Fuel Rod Ticking At: " + level.getGameTime());
        level.scheduleTick(pos,this,20);
    }

    public void causeMeltdown(int Heat, BlockPos pos, BlockState state, Level level){
        fireNeutrinosOrthogonally(Heat,pos,level);
        state.setValue(HEAT,Heat/2);
        level.explode(null,pos.getX(),pos.getY(),pos.getZ(),Heat,Level.ExplosionInteraction.TNT);
    }

    public BlockPos getCardinalFromOrdinal(BlockPos start, int ordinal){
        if (ordinal <= 4){
            return start.relative(Direction.values()[ordinal+2]);
        } else {
            Direction dir = Direction.values()[ordinal+2];
            return start.relative(dir).relative(dir.getClockWise(Direction.Axis.Y));
        }
    }

         */


}
