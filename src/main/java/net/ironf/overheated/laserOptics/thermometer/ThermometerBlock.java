package net.ironf.overheated.laserOptics.thermometer;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ThermometerBlock extends Block implements IBE<ThermometerBlockEntity> {
    public ThermometerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<ThermometerBlockEntity> getBlockEntityClass() {
        return ThermometerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThermometerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.THERMOMETER.get();
    }
}
